package yh326.exception;

public class AlreadyDefinedException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public AlreadyDefinedException(int line, int col, String id) {
        super(line, col, id + " has already been defined");
    }

}
