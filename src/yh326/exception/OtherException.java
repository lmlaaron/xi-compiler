package yh326.exception;

public class OtherException extends SemanticCheckException {

    private static final long serialVersionUID = 1L;

    public OtherException(int line, int col, String message) {
        super(line, col, message);
    }

}
