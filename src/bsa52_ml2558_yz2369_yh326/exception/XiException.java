package bsa52_ml2558_yz2369_yh326.exception;

public class XiException extends Exception {

    private static final long serialVersionUID = 1L;

    public int line, col;
    public String message;

    public XiException(int line, int col, String message) {
        super(line + ":" + col + " error: " + message);
        this.line = line;
        this.col = col;
        this.message = message;
    }

    public void print(String fileName) {
        String kind;
        if (this instanceof LexingException) {
            kind = "Lexical";
        } else if (this instanceof ParsingException) {
            kind = "Syntax";
        } else if (this instanceof TypecheckingException) {
            kind = "Semantic";
        } else {
            kind = "Unknown";
        }
        System.out.println(kind + " error at " + fileName + ":" + line + ":" + col + ": " + message);
    }
}
