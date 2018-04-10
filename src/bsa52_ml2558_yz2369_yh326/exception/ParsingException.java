package bsa52_ml2558_yz2369_yh326.exception;

public class ParsingException extends XiException {

    private static final long serialVersionUID = 1L;

    public ParsingException(int line, int col, String message) {
        super(line, col, message);
    }
}
