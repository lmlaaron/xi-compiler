package bsa52_ml2558_yz2369_yh326.exception;

public class NotDefinedException extends TypecheckingException {

    private static final long serialVersionUID = 1L;

    public NotDefinedException(int line, int col, String id) {
        super(line, col, id + " is not defined");
    }

}
