package yh326.ast.node.vardecl;

import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;

public class VarDecl extends Node {
    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
    }
}
