package yh326.ast.node.vardecl;

import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;

public class VarDeclEmp extends Node {
    public VarDeclEmp(int line, int col, String id, TypeNode typeNode) {
        super(line, col, new Identifier(-1,-1, id), typeNode); // -1 in this case because identifiers never
                                                                         // result in errors inside var declarations
    }
}
