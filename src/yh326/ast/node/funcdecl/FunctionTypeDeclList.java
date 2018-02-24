package yh326.ast.node.funcdecl;

import yh326.ast.node.Node;

/**
 * A node that represents a list of return variable declarations.
 * @author Syugen
 *
 */
public class FunctionTypeDeclList extends Node {
    public FunctionTypeDeclList(int line, int col, FunctionTypeDecl varDecl) {
        super(line, col, varDecl);
    }
    
    public FunctionTypeDeclList(int line, int col) {
        super(line, col);
    }
}