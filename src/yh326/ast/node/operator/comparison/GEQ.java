package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.ArithmeticComparisonOperator;

public class GEQ extends ArithmeticComparisonOperator {
    public GEQ(int line, int col) {
        super(line, col, ">=");
    }
}