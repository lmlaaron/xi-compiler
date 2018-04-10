package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.Stmt;

public abstract class Expr extends Stmt {
    /**
     * @param line
     * @param col
     * @param string
     */
    public Expr(int line, int col, String string) {
        super(line, col, string);
    }

    /**
     * This operator is used when creating ArrayLiteral, unary expression, and
     * binary expression.
     * 
     * @param line
     * @param col
     * @param nodes
     */
    public Expr(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

}