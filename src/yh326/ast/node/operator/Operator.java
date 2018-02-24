package yh326.ast.node.operator;

import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.exception.TypeErrorException;

import java.util.Arrays;

//TODO: after modifying cup file, make this and other node super classes abstract
public class Operator extends Node {
    public Operator(int line, int col, String repr) {
        super(line, col, repr);
    }

    /**
     * A utility function for subclasses. Expression Nodes should delegate
     * to this function after typechecking operands.
     *
     * @param operands the inputs for the operator
     * @return the return type of the operation on the given operands
     *
     * @throws  TypeErrorException if operands are not of valid types for this operator,
     *                             or if there are an invalid number of operands, or
     *                             if the types of the operands are not the same
     * @throws  RuntimeException if operands' types were not initialized prior to
     *                           calling this
     */
    public NodeType resultTypeFrom(Node... operands) throws TypeErrorException {
        // validate number of arguments
        if (operands.length < 1)
            throw new RuntimeException("Must have at least one operand!");
        else if (!validNumOperands(operands.length))
            throw new RuntimeException("Invalid number of operands for given operator");

        // make sure all operands have initialized types
        if (!Arrays.stream(operands).allMatch(operand -> operand.decoration.hasType())){
            throw new RuntimeException("Operand type not initialized before typechecking with operator");
        }

        // make sure all operands have same type
        NodeType type = operands[0].decoration.getType();
        if (!Arrays.stream(operands).allMatch(operand -> operand.decoration.getType().equals(type))) {
            throw new TypeErrorException(this, "Operand types don't match!");
        }

        // function implementation checks whether operand type is valid
        return returnTypeForOperandType(type);
    }

    public boolean validNumOperands(int num) {
        throw new RuntimeException("validNumOperands not implemented for class!");
    }
    public NodeType returnTypeForOperandType(NodeType operandType) throws TypeErrorException {
        throw new RuntimeException("returnTypeForOperandType not implemented for class!");
    }
}
