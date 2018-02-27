package yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.operator.Operator;
import yh326.ast.type.NodeType;

public class ExprNonAtom extends Expr {
    private Operator operator;
    private List<Node> operands;
    
    public ExprNonAtom(int line, int col, Node... nodes) {
        super(line, col, nodes);
        operator = (Operator)children.get(0);
        operands = children.subList(1, children.size());
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // to work with the operator, first we need to know the types of the operands
        List<NodeType> operandTypes = new ArrayList<>(operands.size());
        for (Node operand : operands) {
            operandTypes.add(operand.typeCheck(sTable));
        }

        return operator.resultTypeFrom(operandTypes.toArray(new NodeType[operands.size()]));
    }
}
