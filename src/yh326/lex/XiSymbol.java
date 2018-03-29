package yh326.lex;

import yh326.gen.sym;

public class XiSymbol extends java_cup.runtime.Symbol implements sym {
    /**
     * Has the same function as the "left" attribute of its super class "symbol".
     */
    private int line;
    
    /**
     * Has the same function as the "right" attribute of its super class "symbol".
     */
    private int column;
    private String typeString;

    public XiSymbol(int type, String typeString, Object value, int line, int column) {
        super(type, line, column, value);
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