package yh326.exception;

public class TypecheckingException extends XiException {

    private static final long serialVersionUID = 1L;

    public TypecheckingException(int line, int col, String message) {
        super(line, col, message);
    }
}
