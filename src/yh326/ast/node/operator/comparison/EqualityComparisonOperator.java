package yh326.ast.node.operator.comparison;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.operator.comparison.ComparisonOperator;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VariableType;

public class EqualityComparisonOperator extends ComparisonOperator {
    public EqualityComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected Type returnTypeForOperandType(Type operandType) throws TypeErrorException {
        Type intType = new VariableType(PrimitiveType.INT, 0);
        Type boolType = new VariableType(PrimitiveType.BOOL, 0);
        // TODO: Type should have isArray function?
        boolean isArray = operandType instanceof VariableType && ((VariableType)operandType).getLevel() > 0;
        if (operandType.equals(intType) || operandType.equals(boolType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }
}
