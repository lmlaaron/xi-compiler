package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class PrimitiveTypeNode extends TypeNode {

    public PrimitiveTypeNode(int line, int col, String string) {
        super(line, col, string);
    }
    
    /* TODO DUMMY
     */
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableNodeType(PrimitiveNodeType.INT, 3);
    }
}
