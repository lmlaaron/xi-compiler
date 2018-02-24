package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class TypeNode extends Node {

    public TypeNode(int line, int col, String string) {
        super(line, col, string);
    }
    
    public TypeNode(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableNodeType t = (VariableNodeType) children.get(1).typeCheck(sTable);

        if (children.size() <= 2) {
            // Size of array not given
            t.increaseLevel();
        } else {
            // Size of array given. Check if size (expr) is integer.
            VariableNodeType expr = (VariableNodeType) children.get(2).typeCheck(sTable);
            VariableNodeType integer = new VariableNodeType(PrimitiveNodeType.INT);
            if (expr.equals(integer)) {
                t.increaseLevel((Expr) children.get(2));
            } else {
                throw new TypeErrorException(integer, expr);
            }
        }
        
        return t;
    }

}
