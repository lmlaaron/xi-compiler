package yh326.ast.node.funcdecl;

import yh326.ast.node.Node;
import yh326.ast.node.stmt.VarDecl;

/**
 * A node that represents a list of return variable declarations.
 * @author Syugen
 *
 */
public class FunctionTypeDeclList extends Node {
    /**
     * Constructor
     * @param line
     * @param col
     * @param varDecl
     */
    public FunctionTypeDeclList(int line, int col, VarDecl varDecl) {
        super(line, col, varDecl);
    }
    
    /**
     * Constructor
     * @param line
     * @param col
     */
    public FunctionTypeDeclList(int line, int col) {
        super(line, col);
    }
}
