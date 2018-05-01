package bsa52_ml2558_yz2369_yh326.ast.node.operator;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.Arrays;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.exception.OperandTypeException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.TypeInconsistentException;

public class ObjectOperator extends Operator {
    public ObjectOperator(int line, int col) {
        super(line, col, ".");
    }    
}
