package yh326.ast.node.operator.comparison;

public class Equal extends EqualityComparisonOperator {
    public Equal(int line, int col) {
        super(line, col, "==");
    }
}