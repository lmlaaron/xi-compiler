package yh326.ast.node.operator.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class Times extends ArithmeticOperator {
    public Times(int line, int col) {
        super(line, col, "*");
    }
    
    @Override
    public IRNode translate() {
    	return new IRBinOp(OpType.MUL, (IRExpr) children.get(1).translate(), (IRExpr) children.get(2).translate());
    }
}
