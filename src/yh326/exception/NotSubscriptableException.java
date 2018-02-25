package yh326.exception;

import yh326.ast.type.NodeType;

public class NotSubscriptableException extends Exception {
   
    private static final long serialVersionUID = 1L;

    public NotSubscriptableException(NodeType t) {
        super(t + " is not subscriptable.");
    }

}
