package bsa52_ml2558_yz2369_yh326.ast.node.operator;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.Arrays;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.exception.OperandTypeException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.TypeInconsistentException;

//TODO: after modifying cup file, make this and other node super classes abstract
public abstract class Operator extends Node {
    public Operator(int line, int col, String repr) {
        super(line, col, repr);
    }

    /**
     * A utility function for subclasses. Expression Nodes should delegate to this
     * function after typechecking operands.
     *
     * @param operandTypes
     *            the type for each operand
     * @return the return type of the operation on the given operands
     *
     * @throws OperandTypeException
     *             if operands are not of valid types for this operator, or if there
     *             are an invalid number of operands, or if the types of the
     *             operands are not the same
     */
    public NodeType resultTypeFrom(NodeType... operandTypes) throws Exception {
        // ensure all types are the same
        boolean typesAreSame = Arrays.stream(operandTypes).allMatch(type -> type.equals(operandTypes[0]));
        if (!typesAreSame)
            throw new TypeInconsistentException(line, col, "Operands");
        // ensure operator accepts given operand types
        if (!validNumOperands(operandTypes.length))
            throw new OtherException(line, col,
                    "Operator " + this.value + "does not accept " + operandTypes.length + " operands");

        // function implementation checks whether operand type is valid
        return returnTypeForOperandType(operandTypes[0]);
    }

    /**
     * Similar to Node.translate, translates the given operator and operands into IR
     * representation. This separate function, like others of this class, is needed
     * because an operator's operands are siblings rather than children
     *
     * @param operands
     *            all operands for the given operator
     * @return the IR subtree corresponding the this operation
     */
    public abstract IRNode translateWithOperands(IRExpr... operands);

    /**
     * function for specifying unary/binary-ness in subclasses
     *
     * @param num
     *            the number of operands attempted for the given operator
     * @returns whether num is an acceptable number of operands
     * @throws RuntimeException
     *             if not implemented by subclasses
     */
    public abstract boolean validNumOperands(int num);

    /**
     * function for mapping acceptable operand types to corresponding return types
     *
     * @param operandType
     *            the type of ALL operands
     * @return the type of the return value of the operation
     * @throws OperandTypeException
     *             if the operand type is not accepted by this operator
     */
    public abstract NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException;
}
