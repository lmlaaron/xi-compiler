package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class Plus extends ArithmeticOperator {
    public Plus(int line, int col) {
        super(line, col, "+");
    }
}
