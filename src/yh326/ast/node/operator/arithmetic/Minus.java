package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class Minus extends ArithmeticOperator {
    public Minus(int line, int col) {
        super(line, col, "-");
    }
}
