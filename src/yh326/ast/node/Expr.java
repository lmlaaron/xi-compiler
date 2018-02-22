package yh326.ast.node;

public class Expr extends Node {
    protected Node left;
    protected Node right;

    public Expr(Node operator, Node left, Node right) {
        super(operator, left, right);
    }
    public Expr(Node operator, Node node) {
        super(operator, node);
    }
}