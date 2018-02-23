package yh326.ast.node.operator.comparison;

import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class EqualityComparisonOperator extends ComparisonOperator {
    public EqualityComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
        NodeType intType = new VariableNodeType(PrimitiveNodeType.INT, 0);
        NodeType boolType = new VariableNodeType(PrimitiveNodeType.BOOL, 0);
        // TODO: NodeType should have isArray function?
        boolean isArray = operandType instanceof VariableNodeType && ((VariableNodeType)operandType).getLevel() > 0;
        if (operandType.equals(intType) || operandType.equals(boolType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }
}
