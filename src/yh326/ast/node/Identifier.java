package yh326.ast.node;

import yh326.ast.node.Node;

public class Identifier extends Node {
    public Identifier(int line, int col, String id) {
        super(line, col, id);
    }
}
