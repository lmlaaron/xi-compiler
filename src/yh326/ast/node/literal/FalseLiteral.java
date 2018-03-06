package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class FalseLiteral extends ExprAtom {

    /**
     * Constructor
     * @param line
     * @param col
     */
    public FalseLiteral(int line, int col) {
        super(line, col, "false");
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableType(Primitives.BOOL);
    }
}
