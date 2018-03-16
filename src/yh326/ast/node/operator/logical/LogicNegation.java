package yh326.ast.node.operator.logical;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class LogicNegation extends LogicalOperator {
    public LogicNegation(int line, int col) {
        super(line, col, "!");
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 1;
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        return new IRBinOp(OpType.XOR, new IRConst(1), operands[0]);
    }
}
