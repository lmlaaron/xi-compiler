package yh326.util;

import yh326.gen.sym;

public class XiSymbol extends java_cup.runtime.Symbol implements sym {
    private int line;
    private int column;
    private String typeString;

    public XiSymbol(int type, String typeString, Object value, int line, int column) {
        super(type, -1, -1, value);
        this.line = line;
        this.column = column;
        this.typeString = typeString;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return line + ":" + column + (typeString == null ? "" : " " + typeString) + " " + value;
    }

}
