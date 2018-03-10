package yh326.ast.node.expr;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.TypeInconsistentException;

public class ArrayLiteral extends ExprAtom {

    /**
     * Constructor
     * @param line
     * @param col
     */
    public ArrayLiteral(int line, int col) {
        super(line, col);
    }

//    @Override
//    public IRNode translate() {
//        // The children attribute must be an expression list, per the cup file
//        // TODO: implement the following:
//        /*
//        SEQ{
//            CALL {
//                _xi_alloc,
//                *** arrlen*8 + 8 ***
//            },
//            SEQ {
//                *** assignment of each individual value here ***
//            }
//        }
//         */
//    }
    
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
}
