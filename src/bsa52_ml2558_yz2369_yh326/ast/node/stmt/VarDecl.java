package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class VarDecl extends Stmt {
    private Identifier id;
    private TypeNode typeNode;
    private List<Expr> sizes;

    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.id = id;
        this.typeNode = typeNode;
    }

    public Identifier getId() {
        return id;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        addVarToTable(sTable, id.value);
        return new UnitType();
    }

    public NodeType typeCheckAndReturn(SymbolTable sTable) throws Exception {
        return addVarToTable(sTable, id.value);
    }

    public VariableType addVarToTable(SymbolTable sTable, String id) throws Exception {
        // Get NodeType from typeNode
        // For example, from Node ([] int) get NodeType int[]
        VariableType t = (VariableType) typeNode.typeCheck(sTable);
        // Add the combination to the context.
        // If it's already in the context, an exception is thrown.
        if (sTable.addVar(id, t) == false) {
            throw new AlreadyDefinedException(line, col, id);
        }
        sizes = t.getSizes();
        return t;
    }

    @Override
    public IRNode translate() {
        List<IRStmt> stmts = new ArrayList<IRStmt>();
        List<IRExpr> sizesExpr = new ArrayList<IRExpr>();

        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i) == null)
                break;

            IRExpr size = (IRExpr) sizes.get(i).translate();
            if (size instanceof IRConst || size instanceof IRTemp || size instanceof IRName) {
                sizesExpr.add(size);
            } else {
                IRTemp sizeTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                stmts.add(new IRMove(sizeTemp, size));
                sizesExpr.add(sizeTemp);
            }

        }

        IRExpr t = generateIRNode(sizesExpr, 0);
        IRTemp var = new IRTemp(id.getId());
        if (t == null) {
            return var;
        } else {
            stmts.add(new IRMove(var, t));
            return new IRESeq(new IRSeq(stmts), var);
        }
    }

    private IRExpr generateIRNode(List<IRExpr> sizesExpr, int i) {
        if (i >= sizes.size() || sizes.get(i) == null) {
            return null;
        }

        List<IRStmt> stmts = new ArrayList<IRStmt>();
        String labelNumber = NumberGetter.uniqueNumber();
        IRTemp newArray = new IRTemp("_array_" + labelNumber);

        // Allocate an array with size + 1 (each unit is 8 bytes)
        IRBinOp irSize = new IRBinOp(OpType.MUL, sizesExpr.get(i), new IRConst(8));
        IRCall call = new IRCall(new IRName("_xi_alloc"), new IRBinOp(OpType.ADD, irSize, new IRConst(8)));
        stmts.add(new IRMove(newArray, new IRBinOp(OpType.ADD, call, new IRConst(8))));

        // Length is located at index of -1
        IRBinOp indexNegOne = new IRBinOp(OpType.SUB, newArray, new IRConst(8));
        stmts.add(new IRMove(new IRMem(indexNegOne), sizesExpr.get(i)));

        // The last bracket with value
        if (i + 1 >= sizes.size() || sizes.get(i + 1) == null) {
            return new IRESeq(new IRSeq(stmts), newArray);
        } else {
            IRTemp curIndex = new IRTemp("_index_" + labelNumber);
            stmts.add(new IRMove(curIndex, new IRConst(0)));
            // the condition of the loop
            IRBinOp cond = new IRBinOp(OpType.LT, curIndex, sizesExpr.get(i));
            // the content of the loop
            List<IRStmt> then = new ArrayList<IRStmt>();
            IRMem memTo = new IRMem(
                    new IRBinOp(OpType.ADD, newArray, new IRBinOp(OpType.MUL, curIndex, new IRConst(8))));
            IRExpr memFrom = generateIRNode(sizesExpr, i + 1);
            then.add(new IRMove(memTo, memFrom));
            then.add(new IRMove(curIndex, new IRBinOp(OpType.ADD, curIndex, new IRConst(1))));
            stmts.add(While.getIRWhile(cond, new IRSeq(then)));
            return new IRESeq(new IRSeq(stmts), newArray);
        }
    }
}
