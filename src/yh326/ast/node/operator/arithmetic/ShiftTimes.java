package yh326.ast.node.operator.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class ShiftTimes extends ArithmeticOperator {
    public ShiftTimes(int line, int col) {
        super(line, col, "*>>");
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        // TODO: need to double check if HMUL refers to high multiplication, if true, then "ShiftTimes" is a bad name.
        return new IRBinOp(OpType.HMUL, operands[0], operands[1]);
    }

}