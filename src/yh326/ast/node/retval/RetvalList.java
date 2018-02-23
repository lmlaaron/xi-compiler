package yh326.ast.node.retval;

import yh326.ast.node.Node;
import yh326.ast.node.type.Type;

/**
 * A node that represents a list of return value types.
 * @author Syugen
 *
 */
public class RetvalList extends Node {
    public RetvalList(Type t) {
        super(t);
    }
}
