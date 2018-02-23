package yh326.ast.node.vardecl;

import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;

public class VarDeclEmp extends Node {
    public VarDeclEmp(String id, TypeNode typeNode) {
        super(new Identifier(id), typeNode);
    }
}
