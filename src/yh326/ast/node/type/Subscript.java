package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.node.Bracket;
import yh326.ast.node.Node;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.NotSubscriptableException;
import yh326.exception.TypeErrorException;

public class Subscript extends Expr {

    public Subscript(int line, int col, Node node) {
        // Node can only be identifier, method call, subscript.
        super(line, col, new Bracket(line, col), node);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = (VariableType) children.get(1).typeCheck(sTable);
        
        // Check if size (expr) is integer.
        VariableType expr = (VariableType) children.get(2).typeCheck(sTable);
        VariableType integer = new VariableType(Primitives.INT);
        
        if (expr.equals(integer)) {
            if (!t.decreaseLevel()) {
                throw new NotSubscriptableException(t);
            }
            return t;
        } else {
            throw new TypeErrorException(integer, expr);
        }
    }

}
