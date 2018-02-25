package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;

public class Length extends Expr {

    public Length(int line, int col, Expr expr) {
        super(line, col, new Keyword(line, col, "length"), expr);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        throw new RuntimeException("Not implemented!!!");
    }
}
