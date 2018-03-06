package yh326.exception;

public class LexingException extends XiException {

    private static final long serialVersionUID = 1L;

    public LexingException(int line, int col, String message) {
    		super(line, col, message);
    }
}
