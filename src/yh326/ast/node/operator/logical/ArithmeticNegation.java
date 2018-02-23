package yh326.ast.node.operator.logical;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class ArithmeticNegation extends ArithmeticOperator {
    public ArithmeticNegation() {
        super("-");
    }

    @Override
    protected boolean validNumOperands(int num) {
        return num == 1; // must override binary default
    }
}
