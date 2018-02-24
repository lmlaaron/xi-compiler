package yh326.ast.node.operator.logical;

import yh326.ast.node.operator.Operator;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class LogicalOperator extends Operator {
    public LogicalOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
        NodeType boolType = new VariableNodeType(PrimitiveNodeType.BOOL, 0);
        if (operandType.equals(boolType)) {
            return boolType;
        }
        else {
            throw new TypeErrorException(this, "Arithmetic operator only works on int");
        }
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 2; // binary by default
    }
}
