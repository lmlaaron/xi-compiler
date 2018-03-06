package yh326.exception;

public class TypeInconsistentException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public TypeInconsistentException(int line, int col, String message) {
        super(line, col, message + " type inconsistent");
    }

}
