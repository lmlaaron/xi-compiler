package yh326.ast.node.operator.arithmetic;

public class ArithmeticNegation extends ArithmeticOperator {
    public ArithmeticNegation() {
        super("-");
    }

    @Override
    public boolean validNumOperands(int num) {
        return num == 1; // must override binary default
    }
}
