package yh326.ast.node.operator.comparison;

import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;

public class Equal extends EqualityComparisonOperator {
    public Equal(int line, int col) {
        super(line, col, "==");
    }
}