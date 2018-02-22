package yh326.ast.node;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VarType;

public class EqualityComparisonOperator extends ComparisonOperator {
    public EqualityComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected Type returnTypeForOperandType(Type operandType) throws TypeErrorException {
        Type intType = new VarType(PrimitiveType.INT, 0);
        Type boolType = new VarType(PrimitiveType.BOOL, 0);
        // TODO: Type should have isArray function?
        boolean isArray = operandType instanceof VarType && ((VarType)operandType).getLevel() > 0;
        if (operandType.equals(intType) || operandType.equals(boolType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }
}
