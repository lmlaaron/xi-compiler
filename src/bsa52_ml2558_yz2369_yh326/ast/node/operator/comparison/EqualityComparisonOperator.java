package bsa52_ml2558_yz2369_yh326.ast.node.operator.comparison;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.exception.OperandTypeException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.TypecheckingException;

public abstract class EqualityComparisonOperator extends ComparisonOperator {
    public EqualityComparisonOperator(int line, int col, String repr) {
        super(line, col, repr);
    }
    
    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        throw new RuntimeException("This shouldn't be called");
    }

    @Override
    public NodeType returnTypeForOperandType(SymbolTable sTable, boolean containsNull, NodeType operandType) throws TypecheckingException {
        NodeType intType = new PrimitiveType(Primitives.INT);
        NodeType boolType = new PrimitiveType(Primitives.BOOL);

        if (operandType.equals(intType) || operandType.equals(boolType)) {
            return boolType;
        } else if (operandType instanceof VariableType && ((VariableType) operandType).getLevel() > 0) {
            return boolType;
        } else if (operandType instanceof ObjectType) {
            if (containsNull) {
                return boolType;
            } else if (sTable.getCurClass() != null &&
                    sTable.getCurClass().classId.value.equals(((ObjectType) operandType).getType().classId.value)) {
                return boolType;
            } else {
                throw new OtherException(line, col, "Cannot compare object outside class.");
            }
        } else {
            throw new OperandTypeException(line, col, value, "int");
        }
    }
}
