package bsa52_ml2558_yz2369_yh326.exception;

public class TypeInconsistentException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public TypeInconsistentException(int line, int col, String message) {
        super(line, col, message + " type inconsistent");
    }

}
