package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

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
