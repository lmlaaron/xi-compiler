package yh326.ast.node.operator.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;

public class ArithmeticNegation extends ArithmeticOperator {
    public ArithmeticNegation(int line, int col) {
        super(line, col, "-");
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 1; // must override binary default
    }
    
    @Override
    public IRNode translate() {
    	return new IRBinOp(OpType.SUB, new IRConst(0), (IRExpr) children.get(1).translate());
    }
}
