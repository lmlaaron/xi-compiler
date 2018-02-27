package yh326.exception;

public class NotDefinedException extends SemanticCheckException {

    private static final long serialVersionUID = 1L;
    
    public NotDefinedException(int line, int col, String id) {
        super(line, col, id + " is not defined");
    }


}
