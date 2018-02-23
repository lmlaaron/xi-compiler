package yh326.ast.node.vardecl;

import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.type.Type;

public class VarDeclEmp extends Node {
    public VarDeclEmp(String id, Type type) {
        super(new Identifier(id), type);
    }
}
