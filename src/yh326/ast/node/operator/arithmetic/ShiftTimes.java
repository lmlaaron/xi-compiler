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
    public IRNode translate() {
    	// TODO: need to double check if HMUL refers to high multiplication, if true, then "ShiftTimes" is a bad name.
    	return new IRBinOp(OpType.HMUL, (IRExpr) children.get(1).translate(), (IRExpr) children.get(2).translate());
    }
}