package yh326.ast.node.operator.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import yh326.ast.node.operator.comparison.EqualityComparisonOperator;

public class NotEqual extends EqualityComparisonOperator {
    public NotEqual(int line, int col) {
        super(line, col, "!=");
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        // TODO: this won't work on arrays
        return new IRBinOp(OpType.NEQ, operands[0], operands[1]);
    }

}