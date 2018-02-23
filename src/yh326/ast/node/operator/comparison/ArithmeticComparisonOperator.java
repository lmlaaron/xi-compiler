package yh326.ast.node.operator.comparison;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.operator.comparison.ComparisonOperator;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VariableType;

public class ArithmeticComparisonOperator extends ComparisonOperator {
    public ArithmeticComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected Type returnTypeForOperandType(Type operandType) throws TypeErrorException {
        Type intType = new VariableType(PrimitiveType.INT, 0);
        Type boolType = new VariableType(PrimitiveType.BOOL, 0);
        if (operandType.equals(intType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }
}
