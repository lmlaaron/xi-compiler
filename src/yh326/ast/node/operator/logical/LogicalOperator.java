package yh326.ast.node.operator.logical;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.operator.Operator;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VarType;

public class LogicalOperator extends Operator {
    public LogicalOperator(String repr) {
        super(repr);
    }

    @Override
    protected Type returnTypeForOperandType(Type operandType) throws TypeErrorException {
        Type boolType = new VarType(PrimitiveType.BOOL, 0);
        if (operandType.equals(boolType)) {
            return boolType;
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
