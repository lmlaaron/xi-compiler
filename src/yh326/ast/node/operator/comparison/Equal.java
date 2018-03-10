package yh326.ast.node.operator.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class Equal extends EqualityComparisonOperator {
    public Equal(int line, int col) {
        super(line, col, "==");
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        // TODO: won't work on arrays
        return new IRBinOp(OpType.EQ, operands[0], operands[1]);
    }
    
    @Override
    public IRNode translate() {
    	return new IRBinOp(OpType.EQ, (IRExpr) children.get(1).translate(), (IRExpr) children.get(2));
    }
}