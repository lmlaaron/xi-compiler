package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class TypeNode extends Node {

    public TypeNode(int line, int col, String string) {
        super(line, col, string);
    }
    
    public TypeNode(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType t = children.get(1).typeCheck(sTable);
        if (t instanceof VariableNodeType) {
            ((VariableNodeType) t).setLevel(((VariableNodeType) t).getLevel() + 1);
            return t;
        } else {
            throw new RuntimeException("Unexpected Error");
        }
    }

}
