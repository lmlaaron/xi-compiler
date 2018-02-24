package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class CharacterLiteral extends ExprAtom {

    public CharacterLiteral(int line, int col, String ch) {
        super(line, col, ch);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableNodeType(PrimitiveNodeType.INT, 0);
    }

}
