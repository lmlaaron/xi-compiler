package yh326.exception;

public class SemanticCheckException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public SemanticCheckException(int line, int col, String message) {
        super(line + ":" + col + " error:" + message);
    }

}
