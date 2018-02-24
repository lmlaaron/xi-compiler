package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.ArithmeticComparisonOperator;

public class LT extends ArithmeticComparisonOperator {
    public LT(int line, int col) {
        super(line, col, "<");
    }
}