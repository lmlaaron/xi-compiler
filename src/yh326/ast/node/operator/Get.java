package yh326.ast.node.operator;

import yh326.ast.node.Node;

public class Get extends Node {

    public Get(int line, int col) {
        super(line, col, "=");
    }

}
