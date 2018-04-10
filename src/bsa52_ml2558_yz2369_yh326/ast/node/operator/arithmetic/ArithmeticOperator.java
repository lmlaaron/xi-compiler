package bsa52_ml2558_yz2369_yh326.ast.node.operator.arithmetic;

import bsa52_ml2558_yz2369_yh326.ast.node.operator.Operator;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.OperandTypeException;

public abstract class ArithmeticOperator extends Operator {
    public ArithmeticOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        NodeType intType = new VariableType(Primitives.INT);
        if (operandType.equals(intType)) {
            return intType;
        } else {
            throw new OperandTypeException(line, col, value, "int");
        }
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 2; // binary by default
    }
}
