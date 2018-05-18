package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.exception.TypeInconsistentException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

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
        VariableType type, arrayType;
        if (children.size() == 0) {
            type = new PrimitiveType(Primitives.EMPTY);
        } else {
            type = (VariableType) children.get(0).typeCheck(sTable);
        }
        for (int i = 1; i < children.size(); i++) {
            VariableType next = (VariableType) children.get(i).typeCheck(sTable);
            if (type.isSubclassOf(next)) {
                type = next;
            } else if (next.isSubclassOf(type)) {
            } else if (!type.equals(next)) {
                throw new TypeInconsistentException(line, col, "Array literal");
            }
        }
        
        arrayType = type.copy();
        arrayType.increaseLevel();
        return arrayType;
    }

    @Override
    public IRNode translate() {
        IRESeq newArrayESeq = Utilities.xiAlloc(new IRConst(children.size()));
        List<IRStmt> stmts = ((IRSeq) newArrayESeq.stmt()).stmts();
        IRExpr newArray = newArrayESeq.expr();

        for (int i = 0; i < children.size(); i++) {
            IRMem mem = new IRMem(new IRBinOp(OpType.ADD, newArray, new IRConst(i * 8)));
            stmts.add(new IRMove(mem, (IRExpr) children.get(i).translate()));
        }
        return new IRESeq(new IRSeq(stmts), newArray);
    }
}
