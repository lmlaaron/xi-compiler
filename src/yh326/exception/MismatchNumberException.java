package yh326.exception;

public class MismatchNumberException extends SemanticCheckException {

    private static final long serialVersionUID = 1L;

    public MismatchNumberException(int line, int col, int expect, int actual) {
        super(line, col, "Mismatched number of values. Expected " + expect + ", but found " + actual);
    }

}
