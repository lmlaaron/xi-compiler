package yh326.ast.node.expr;


public class ExprAtom extends Expr {

    public ExprAtom(int line, int col, String value) {
        super(line, col, value);
    }
    
    public ExprAtom(int line, int col, Expr... exprs) {
        super(line, col, exprs);
    }
    
    @Override
    public String toString() {
        return value;
    }

}
