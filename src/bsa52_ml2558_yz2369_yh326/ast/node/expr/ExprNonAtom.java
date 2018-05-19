package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.operator.Operator;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class ExprNonAtom extends Expr {
    private Operator operator;
    private List<Node> operands;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param nodes
     */

    public ExprNonAtom(int line, int col, Node... nodes) {
        super(line, col, nodes);
        operator = (Operator) children.get(0);
        operands = children.subList(1, children.size());
    }

    @Override
    public IRNode translate() {
    		if (this.operator.value.equals(".")) {
    			// TODO need to implement dynamic dispatch here
    			return null;
    		} else {
    			IRExpr[] translatedOperands = new IRExpr[operands.size()];
    			for (int i = 0; i < operands.size(); i++)
    				translatedOperands[i] = (IRExpr) operands.get(i).translate();
    			return operator.translateWithOperands(translatedOperands);
    		}
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // to work with the operator, first we need to know the types of the operands
        List<NodeType> operandTypes = new ArrayList<>(operands.size());
        for (Node operand : operands) {
            operandTypes.add(operand.typeCheck(sTable));
        }

        return operator.resultTypeFrom(sTable, operandTypes.toArray(new NodeType[operands.size()]));
    }
}
