package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;

/**
 * Helper class that helps to manage multi-values in LHS
 * 
 * @author Syugen
 *
 */
public class AssignToList extends Node {

    public AssignToList(int line, int col, Node node) {
        super(line, col, node);
    }

    public AssignToList(int line, int col, Node... node) {
        super(line, col, node);
    }

}
