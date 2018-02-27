package yh326.ast.node.operator;

import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.exception.OperandTypeException;
import yh326.exception.OtherException;
import yh326.exception.TypeInconsistentException;

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
    public NodeType resultTypeFrom(NodeType... operandTypes) throws Exception {
        //ensure all types are the same
        boolean typesAreSame= Arrays.stream(operandTypes).allMatch(type -> type.equals(operandTypes[0]));
        if (!typesAreSame)
            throw new TypeInconsistentException(line, col, "Operands");
        // ensure operator accepts given operand types
        if (!validNumOperands(operandTypes.length))
            throw new OtherException(line, col, "Operator " + this.value + "does not accept " + operandTypes.length + " operands");


        // function implementation checks whether operand type is valid
        return returnTypeForOperandType(operandTypes[0]);
    }

    public boolean validNumOperands(int num) {
        throw new RuntimeException("validNumOperands not implemented for class!");
    }
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        throw new RuntimeException("returnTypeForOperandType not implemented for class!");
    }
}
