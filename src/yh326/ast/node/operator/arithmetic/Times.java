package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class Times extends ArithmeticOperator {
    public Times(int line, int col) {
        super(line, col, "*");
    }
}