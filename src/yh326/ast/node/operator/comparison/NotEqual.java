package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.EqualityComparisonOperator;

public class NotEqual extends EqualityComparisonOperator {
    public NotEqual(int line, int col) {
        super(line, col, "!=");
    }

}