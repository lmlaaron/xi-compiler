package yh326.ast.node.use;

import yh326.ast.node.Identifier;
import yh326.ast.node.Keyword;
import yh326.ast.node.Node;

public class Use extends Node {
    public Use(String id) {
        super(new Keyword("use"), new Identifier(id));
    }
}
