package yh326.ast.node.operator.arithmetic;

import yh326.ast.node.operator.arithmetic.ArithmeticOperator;

public class ShiftTimes extends ArithmeticOperator {
    public ShiftTimes(int line, int col) {
        super(line, col, "*>>");
    }
}