package yh326.ast.node.operator.comparison;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class ArithmeticComparisonOperator extends ComparisonOperator {
    public ArithmeticComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
        NodeType intType = new VariableNodeType(PrimitiveNodeType.INT, 0);
        NodeType boolType = new VariableNodeType(PrimitiveNodeType.BOOL, 0);
        if (operandType.equals(intType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }
}
