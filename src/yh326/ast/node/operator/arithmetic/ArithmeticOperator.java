package yh326.ast.node.operator.arithmetic;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.operator.Operator;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VariableType;

public class ArithmeticOperator extends Operator {
    public ArithmeticOperator(String repr) {
        super(repr);
    }

    @Override
    protected Type returnTypeForOperandType(Type operandType) throws TypeErrorException {
        Type intType = new VariableType(PrimitiveType.INT, 0);
        if (operandType.equals(intType)) {
            return intType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }

    @Override
    protected boolean validNumOperands(int num) {
        return num == 2; // binary by default
    }
}
