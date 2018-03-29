package yh326.ast.node.expr;

public abstract class ExprAtom extends Expr {

    /**
     * Constructor for Character/Integer/String/True/FalseLiteral.
     * 
     * @param line
     * @param col
     * @param value
     */
    public ExprAtom(int line, int col, String value) {
        super(line, col, value);
    }

    /**
     * Constructor for ArrayLiteral.
     * 
     * @param line
     * @param col
     */
    public ExprAtom(int line, int col) {
        super(line, col);
    }

    @Override
    public String toString() {
        return value;
    }

}
