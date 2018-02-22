package yh326.ast.exception;

import yh326.ast.type.VarType;

public class TypeErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public TypeErrorException(VarType a, VarType b) {
        super("Type should be " + a + ", but is " + b);
    }
}
