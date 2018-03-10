package yh326.ast.node.operator.comparison;

import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;

public abstract class ArithmeticComparisonOperator extends ComparisonOperator {
    public ArithmeticComparisonOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        NodeType intType = new VariableType(Primitives.INT);
        NodeType boolType = new VariableType(Primitives.BOOL);
        if (operandType.equals(intType)) {
            return boolType;
        }
        else {
            throw new OperandTypeException(line, col, value, "int");
        }
    }
}
