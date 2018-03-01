package yh326.ast.node;

import yh326.ast.node.Node;

public class Get extends Node {
    public Get(int line, int col) {
        super(line, col, "=");
    }
}
