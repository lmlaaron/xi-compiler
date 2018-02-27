package yh326.ast.node;

import yh326.ast.SymbolTable;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.exception.NotDefinedException;
import yh326.exception.OtherException;

public class Identifier extends Expr {
    private String id;
    
    public Identifier(int line, int col, String id) {
        super(line, col, id);
        this.id = id;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType type = sTable.getVariableType(id);
        if (type != null) {
            return type;
        } else if (sTable.getFunctionType(id) != null) {
            throw new OtherException(line, col, id + " is not a variable");
        } else {
            throw new NotDefinedException(line, col, id);
        }
    }
}
