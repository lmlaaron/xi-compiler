package yh326.ast.node.operator.comparison;

import yh326.ast.node.operator.Operator;

public class ComparisonOperator extends Operator {
    public ComparisonOperator(int line, int col, String repr) {
        super(line, col, repr);
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 2; // all comparisons are binary
    }
}
