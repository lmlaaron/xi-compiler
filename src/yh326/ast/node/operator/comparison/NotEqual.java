package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.EqualityComparisonOperator;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;

public class NotEqual extends EqualityComparisonOperator {
    public NotEqual(int line, int col) {
        super(line, col, "!=");
    }

}