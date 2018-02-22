package yh326.ast.node;

public class ArithmeticNegation extends ArithmeticOperator {
    public ArithmeticNegation() {
        super("-");
    }

    @Override
    protected boolean validNumOperands(int num) {
        return num == 1; // must override binary default
    }
}
