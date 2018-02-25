package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;

public class MethodCall extends Expr {

    public MethodCall(int line, int col, Identifier id) {
        super(line, col, id);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        throw new RuntimeException("Not implemented!!!");
    }
}
