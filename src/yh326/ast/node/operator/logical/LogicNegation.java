package yh326.ast.node.operator.logical;

public class LogicNegation extends LogicalOperator {
    public LogicNegation() { super("-"); }

    @Override
    protected boolean validNumOperands(int num) {
        return num == 1;
    }
}
