package yh326.ast.node.interfc;

import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.vardecl.VarDeclList;

public class Interface extends Node {
    public Interface(String id, VarDeclList args, RetvalList rets) {
        super(new Identifier(id), args, rets);
    }
}
