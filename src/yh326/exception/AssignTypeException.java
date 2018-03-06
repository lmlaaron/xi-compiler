package yh326.exception;

import yh326.ast.type.NodeType;

public class AssignTypeException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public AssignTypeException(int line, int col, NodeType t1, NodeType t2) {
        super(line, col, "Cannot assign " + t1 + " to " + t2);
    }

}
