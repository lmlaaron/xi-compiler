package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class Modulus extends ArithmeticOperator {
    public Modulus(int line, int col) {
        super(line, col, "%");
    }
}