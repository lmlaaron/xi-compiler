package yh326.ast.node;

public class ComparisonOperator extends Operator {
    public ComparisonOperator(String repr) {
        super(repr);
    }

    @Override
    protected boolean validNumOperands(int num) {
        return num == 2; // all comparisons are binary
    }
}
