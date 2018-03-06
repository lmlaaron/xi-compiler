package yh326.ast.node.literal;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class StringLiteral extends ExprAtom {

    /**
     * Constructor
     * @param line
     * @param col
     * @param str
     */
    public StringLiteral(int line, int col, String str) {
        super(line, col, "\"" + str + "\"");
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = new VariableType(Primitives.INT, 1);
        return t;
    }
}
