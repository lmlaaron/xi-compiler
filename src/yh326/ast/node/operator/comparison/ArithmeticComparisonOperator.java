package yh326.ast.node.operator.comparison;

import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class ArithmeticComparisonOperator extends ComparisonOperator {
    public ArithmeticComparisonOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
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
