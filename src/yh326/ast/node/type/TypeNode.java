package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class TypeNode extends Node {

    public TypeNode(String string) {
        super(string);
    }
    
    public TypeNode(Node... nodes) {
        super(nodes);
    }
    
    /* TODO DUMMY
     */
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableNodeType(PrimitiveNodeType.BOOL, 3);
    }

}
