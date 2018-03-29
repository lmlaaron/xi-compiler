package yh326.ast.node.expr;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.node.Node;

public class ExprList extends Node {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param expr
     */
    public ExprList(int line, int col, Expr expr) {
        super(line, col, expr);
    }

    @Override
    public IRNode translate() {
        return children.get(0).translate();
    }

}
