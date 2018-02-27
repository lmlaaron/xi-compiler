package yh326.exception;

public class OperandTypeException extends SemanticCheckException {

    private static final long serialVersionUID = 1L;

    public OperandTypeException(int line, int col, String op, String type) {
        super(line, col, "Operands of " + op + " must be " + type);
    }

}
