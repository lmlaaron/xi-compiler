package yh326.ast.node.operator.comparison;

import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;

public class EqualityComparisonOperator extends ComparisonOperator {
    public EqualityComparisonOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        NodeType intType = new VariableType(Primitives.INT);
        NodeType boolType = new VariableType(Primitives.BOOL);
        // TODO: NodeType should have isArray function?
        //boolean isArray = operandType instanceof VariableType && ((VariableType)operandType).getLevel() > 0;
        if (operandType.equals(intType) || operandType.equals(boolType)) {
            return boolType;
        }
        else {
            throw new OperandTypeException(line, col, value, "int");
        }
    }
}
