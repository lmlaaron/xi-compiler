package yh326.exception;

public class WrongNumberOfArgumentsException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public WrongNumberOfArgumentsException(int size1, int size2) {
        super("Should have " + size1 + "arguments, but have " + size2);
    }
}
