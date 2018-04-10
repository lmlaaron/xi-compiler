package bsa52_ml2558_yz2369_yh326.ast.node.retval;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;

/**
 * A node that represents a list of return value types.
 * 
 * @author Syugen
 *
 */
public class RetvalList extends Node {
    public RetvalList(int line, int col, TypeNode t) {
        super(line, col, t);
    }

    public RetvalList(int line, int col) {
        super(line, col);
    }
}
