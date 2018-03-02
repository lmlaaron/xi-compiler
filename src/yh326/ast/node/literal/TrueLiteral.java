package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class TrueLiteral extends ExprAtom {

    /**
     * Constructor
     * @param line
     * @param col
     */
    public TrueLiteral(int line, int col) {
        super(line, col, "true");
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableType(Primitives.BOOL);
    }
}
