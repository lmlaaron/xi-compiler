package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class PrimitiveTypeNode extends TypeNode {
    private String primitiveType;

    public PrimitiveTypeNode(int line, int col, String primitiveType) {
        super(line, col, primitiveType);
        this.primitiveType = primitiveType;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (primitiveType == "int") {
            return new VariableNodeType(PrimitiveNodeType.INT, 0);
        } else if (primitiveType == "bool") {
            return new VariableNodeType(PrimitiveNodeType.BOOL, 0);
        } else {
            throw new TypeErrorException("Primitive type has to be either int or bool");
        }
    }
}
