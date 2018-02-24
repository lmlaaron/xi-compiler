package yh326.ast.node;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;

public class Identifier extends Expr {
    private String id;
    
    public Identifier(int line, int col, String id) {
        super(line, col, id);
        this.id = id;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return sTable.getVariableType(id);
    }
}
