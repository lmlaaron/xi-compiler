package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.Operator;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class ArithmeticOperator extends Operator {
    public ArithmeticOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
        NodeType intType = new VariableNodeType(PrimitiveNodeType.INT, 0);
        if (operandType.equals(intType)) {
            return intType;
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
