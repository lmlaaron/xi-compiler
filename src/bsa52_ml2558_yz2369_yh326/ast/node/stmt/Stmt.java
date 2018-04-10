/**
 * author: ml2558
 **/
package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;

public abstract class Stmt extends Node {
    public Stmt(int line, int col, String string) {
        super(line, col, string);
    }

    public Stmt(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

}
