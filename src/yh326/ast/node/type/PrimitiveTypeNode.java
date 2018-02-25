package yh326.ast.node.type;

import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;

public class PrimitiveTypeNode extends TypeNode {
    private String primitiveType;

    public PrimitiveTypeNode(int line, int col, String primitiveType) {
        super(line, col, primitiveType);
        this.primitiveType = primitiveType;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (primitiveType == "int") {
            return new VariableType(Primitives.INT);
        } else if (primitiveType == "bool") {
            return new VariableType(Primitives.BOOL);
        } else {
            throw new TypeErrorException("Primitive type has to be either int or bool");
        }
    }
}
