package yh326.ast.node.operator.logical;

public class LogicNegation extends LogicalOperator {
    public LogicNegation(int line, int col) {
        super(line, col, "-");
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 1;
    }
}
