package bsa52_ml2558_yz2369_yh326.ast.node.operator.logical;

import bsa52_ml2558_yz2369_yh326.ast.node.operator.Operator;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.OperandTypeException;

public abstract class LogicalOperator extends Operator {
    public LogicalOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        NodeType boolType = new VariableType(Primitives.BOOL);
        if (operandType.equals(boolType)) {
            return boolType;
        } else {
            throw new OperandTypeException(line, col, value, "bool");
        }
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 2; // binary by default. LogicNegation should override this method
    }
}
