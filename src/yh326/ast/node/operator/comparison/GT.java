package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.comparison.ArithmeticComparisonOperator;

public class GT extends ArithmeticComparisonOperator {
	public GT(int line, int col) {
        super(line, col, ">");
    }
}
