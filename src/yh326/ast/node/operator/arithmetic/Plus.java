package yh326.ast.node.operator.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.node.operator.arithmetic.ArithmeticOperator;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;

public class Plus extends ArithmeticOperator {
    public Plus(int line, int col) {
        super(line, col, "+");
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        // first, check if operand type is an array. plus concatenates arrays
        if (operandType instanceof VariableType && ((VariableType)operandType).getLevel() > 0) {
            return operandType;
        }
        return super.returnTypeForOperandType(operandType);
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        return new IRBinOp(OpType.ADD, operands[0], operands[1]);
    }
}
