package yh326.ast.node.operator.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import yh326.ast.node.operator.comparison.ArithmeticComparisonOperator;

public class GT extends ArithmeticComparisonOperator {
	public GT(int line, int col) {
        super(line, col, ">");
    }
	
	@Override
	public IRNode translate() {
		return new IRBinOp(OpType.GT, (IRExpr) children.get(1).translate(), (IRExpr) children.get(2));
	}
}
