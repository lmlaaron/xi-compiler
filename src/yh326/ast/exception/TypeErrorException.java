package yh326.ast.exception;

import yh326.ast.node.Node;
import yh326.ast.type.NodeType;

public class TypeErrorException extends Exception {

    private static final long serialVersionUID = 1L;

    // bsa52: this is not necessarily a good model for raising the exception. consider the case where
    // we are typechecking an expression -> 1 + true. While we are checking that node, we see that the types
    // of 1 and true are invalid, so we raise an exception. In that case, how would we construct this error?
    //      - for now I'll make a separate constructor
    public TypeErrorException(NodeType a, NodeType b) {
        super("NodeType should be " + a + ", but is " + b);
    }

    public TypeErrorException(Node n, String s) {
        super(s);
    }
}
