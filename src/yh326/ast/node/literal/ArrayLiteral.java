package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;

public class ArrayLiteral extends ExprAtom {

    public ArrayLiteral(int line, int col) {
        super(line, col);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType type;
        if (children.size() == 0) {
            type = new VariableType(Primitives.EMPTY);
        } else {
            type = (VariableType) children.get(0).typeCheck(sTable);
        }
        for (int i = 1; i < children.size(); i++) {
            if (!type.equals(children.get(i).typeCheck(sTable))) {
                throw new TypeErrorException("Array literal type inconsistent");
            }
        }
        type.increaseLevel();
        return type;
    }
}
