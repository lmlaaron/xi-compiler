package yh326.ast.node;

import yh326.ast.SymbolTable;
import yh326.exception.TypeErrorException;
import yh326.ast.node.Node;
import yh326.ast.node.operator.Operator;
import yh326.ast.type.NodeType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Expr extends Node {
    Operator operator;
    List<Node> operands;

    public Expr(int line, int col, Node... nodes) {
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
        // make sure those types are the same:


        if (operator.validNumOperands(operands.size())){ // ensure operator accepts the correct size
            // set operator's type
            operator.decoration.setType(
                operator.resultTypeFrom(
                    operands.toArray(new Node[operands.size()])
                )
            );
            // set this node's type
            decoration.setType(operator.decoration.getType());
            return decoration.getType();
        }
        else {
            throw new TypeErrorException(operator, "Operator " + operator.value + "does not accept " + operands.size() + " operands");
        }
    }
}