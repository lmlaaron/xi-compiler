package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Bracket;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.If;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
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

public class Subscript extends Expr {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param node
     */
    public Subscript(int line, int col, Node node) {
        // Node can only be identifier, method call, subscript.
        super(line, col, new Bracket(line, col), node);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = (VariableType) children.get(1).typeCheck(sTable);

        // Check if size (expr) is integer.
        VariableType expr = (VariableType) children.get(2).typeCheck(sTable);
        VariableType integer = new VariableType(Primitives.INT);

        if (expr.equals(integer)) {
            if (t.getLevel() == 0) {
                throw new OtherException(line, col, t + " is not subscriptable");
            } else {
                // Need to recursively restore the original level
                return new VariableType(t.getType(), t.getLevel() - 1);
            }
        } else {
            throw new MatchTypeException(line, col, integer, expr);
        }
    }

    @Override
    public IRNode translate() {
        List<IRStmt> stmts = new ArrayList<IRStmt>();
        
        IRExpr var = (IRExpr) children.get(1).translate();
        if (var instanceof IRESeq) {
            stmts.add(((IRESeq) var).stmt());
            var = ((IRESeq) var).expr();
        }
        if (var instanceof IRConst || var instanceof IRTemp || var instanceof IRName) {}
        else {
            IRTemp varTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
            stmts.add(new IRMove(varTemp, var));
            var = varTemp;
        }
        
        IRExpr index = (IRExpr) children.get(2).translate();
        if (index instanceof IRESeq) {
            stmts.add(((IRESeq) index).stmt());
            index = ((IRESeq) index).expr();
        }
        if (index instanceof IRConst || index instanceof IRTemp || index instanceof IRName) {}
        else {
            IRTemp indexTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
            stmts.add(new IRMove(indexTemp, index));
            index = indexTemp;
        }
        
        IRMem len = new IRMem(new IRBinOp(OpType.SUB, var, new IRConst(8)));
        IRBinOp lt0 = new IRBinOp(OpType.LT, index, new IRConst(0));
        IRBinOp gtN = new IRBinOp(OpType.GEQ, index, len);
        IRBinOp cond = new IRBinOp(OpType.OR, lt0, gtN);
        IRCall then = new IRCall(new IRName("_xi_out_of_bounds"));
        stmts.add(If.getIRIf(cond, then));
        IRExpr res = new IRMem(new IRBinOp(OpType.ADD, var, 
                new IRBinOp(OpType.MUL, new IRConst(8), index)));
        return new IRESeq(new IRSeq(stmts), res);
    }

}
