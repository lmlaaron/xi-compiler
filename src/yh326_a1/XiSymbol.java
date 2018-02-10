package yh326_a1;

public class XiSymbol extends java_cup.runtime.Symbol implements sym {
    private int line;
    private int column;

    public XiSymbol(int type, Object value, int line, int column) {
        super(type, -1, -1, value);
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return line + ":" + column + " " + value;
    }

}
