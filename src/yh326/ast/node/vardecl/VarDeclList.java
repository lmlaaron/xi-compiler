package yh326.ast.node.vardecl;

import yh326.ast.node.Node;

/**
 * A node that represents a list of return variable declarations.
 * @author Syugen
 *
 */
public class VarDeclList extends Node {
    public VarDeclList(int line, int col, VarDeclFuncDecl varDecl) {
        super(line, col, varDecl);
    }
    
    public VarDeclList(int line, int col) {
        super(line, col);
    }
}
