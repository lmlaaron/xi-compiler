package bsa52_ml2558_yz2369_yh326.exception;

import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;

public class MatchTypeException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public MatchTypeException(int line, int col, NodeType a, NodeType b) {
        super(line, col, "Expected " + a + ", but found " + b);
    }

    public MatchTypeException(int line, int col, String a, NodeType b) {
        super(line, col, "Expected " + a + ", but found " + b);
    }

}
