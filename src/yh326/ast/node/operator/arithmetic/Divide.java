package yh326.ast.node.operator.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class Divide extends ArithmeticOperator {
    public Divide(int line, int col) {
        super(line, col, "/");
    }
    
    @Override
    public IRNode translate() {
    	return new IRBinOp(OpType.DIV, (IRExpr) children.get(1).translate(), (IRExpr) children.get(2).translate());
    }
}