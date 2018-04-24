package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.TypeInconsistentException;
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

public class ArrayLiteral extends ExprAtom {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public ArrayLiteral(int line, int col) {
        super(line, col);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // The following is not specified in the Xi type system
        VariableType t;
        if (children.size() == 0) {
            t = new VariableType(Primitives.EMPTY);
        } else {
            t = (VariableType) children.get(0).typeCheck(sTable);
        }
        for (int i = 1; i < children.size(); i++) {
            if (!t.equals(children.get(i).typeCheck(sTable))) {
                throw new TypeInconsistentException(line, col, "Array literal");
            }
        }
        return new VariableType(t.getType(), t.getLevel() + 1);
    }

    @Override
    public IRNode translate() {
        String name = "_array_" + NumberGetter.uniqueNumberStr();
        List<IRStmt> stmts = new ArrayList<IRStmt>();

        // Allocate an array with size of children + 1 for length (each unit is 8 bytes)
        IRCall call = new IRCall(new IRName("_xi_alloc"), new IRConst(children.size() * 8 + 8));
        stmts.add(new IRMove(new IRTemp(name), new IRBinOp(OpType.ADD, call, new IRConst(8))));

        // Length is located at index of -1
        IRBinOp indexNegOne = new IRBinOp(OpType.SUB, new IRTemp(name), new IRConst(8));
        stmts.add(new IRMove(new IRMem(indexNegOne), new IRConst(children.size())));
        for (int i = 0; i < children.size(); i++) {
            IRMem mem = new IRMem(new IRBinOp(OpType.ADD, new IRTemp(name), new IRConst(i * 8)));
            stmts.add(new IRMove(mem, (IRExpr) children.get(i).translate()));
        }
        return new IRESeq(new IRSeq(stmts), new IRTemp(name));
        // The children attribute must be an expression list, per the cup file
        // TODO: implement the following:
        /*
         * SEQ{ CALL { _xi_alloc, arrlen*8 + 8 *** }, SEQ { assignment of each
         * individual value here *** } }
         */
    }
}
