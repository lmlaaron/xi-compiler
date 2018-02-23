package yh326.ast.node.operator.logical;

import yh326.ast.node.Node;

public class LogicAnd extends LogicalOperator {
    public LogicAnd(Node left, Node right) {
        super("&");
    }
}
