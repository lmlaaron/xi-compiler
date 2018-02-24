package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class IntegerLiteral extends ExprAtom {

    public IntegerLiteral(int line, int col, String value) {
        super(line, col, value);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableNodeType(PrimitiveNodeType.INT);
    }
}
