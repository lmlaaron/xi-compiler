package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class StringLiteral extends ExprAtom {

    public StringLiteral(int line, int col, String str) {
        super(line, col, "\"" + str + "\"");
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableNodeType t = new VariableNodeType(PrimitiveNodeType.INT);
        t.increaseLevel();
        return t;
    }
}
