package yh326.ast.exception;

public class VariableNotDefinedException extends Exception {

    private static final long serialVersionUID = 1L;

    public VariableNotDefinedException(String name) {
        super("Variable \"" + name + "\" is not defined.");
    }

}
