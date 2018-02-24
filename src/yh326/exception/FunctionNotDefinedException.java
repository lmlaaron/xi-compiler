package yh326.exception;

public class FunctionNotDefinedException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public FunctionNotDefinedException(String name) {
        super("Function \"" + name + "\" is not defined.");
    }

}