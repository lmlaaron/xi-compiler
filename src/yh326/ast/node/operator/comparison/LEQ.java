package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.ArithmeticComparisonOperator;

public class LEQ extends ArithmeticComparisonOperator {
    public LEQ(int line, int col) {
        super(line, col, "<=");
    }
}