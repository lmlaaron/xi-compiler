package yh326.ast.node.retval;

import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;

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
