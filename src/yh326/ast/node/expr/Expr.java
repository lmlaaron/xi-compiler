package yh326.ast.node.expr;

import yh326.ast.node.Node;

public class Expr extends Node {
    /**
     * This operator is used when creating an ExprAtom of IntegerLiteral,
     * StringLiteral, CharacterLiteral, TrueLiteral, and FalseLiteral.
     * @param line
     * @param col
     * @param string
     */
    public Expr(int line, int col, String string) {
        super(line, col, string);
    }

    /**
     * This operator is used when creating ArrayLiteral, unary expression,
     * and binary expression.
     * @param line
     * @param col
     * @param nodes
     */
    public Expr(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

}