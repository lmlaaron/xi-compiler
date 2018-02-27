/**
 * author: ml2558
 **/
package yh326.ast.node.stmt;

import yh326.ast.node.Node;

public class Stmt extends Node {
    public Stmt(int line, int col, String string) {
        super(line, col, string);
    }
    
    public Stmt(int line, int col, Node... nodes) {
        super(line,col,nodes);
    }
    
}
