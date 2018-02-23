package yh326.ast.node.stmt;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VariableType;

public class EMPTY extends Stmt {

    public Type typeCheck() throws TypeErrorException {
        if (this.value=="{}") {
            return new VariableType(PrimitiveType.UNIT);
        }
    }
}