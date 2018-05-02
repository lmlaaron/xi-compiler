package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;

public class PrimitiveTypeNode extends TypeNode {
    private String primitiveType;

    public PrimitiveTypeNode(int line, int col, String primitiveType) {
        super(line, col, primitiveType);
        this.primitiveType = primitiveType;
    }
    
    public PrimitiveTypeNode(int line, int col, Identifier type) {
        super(line, col, type.value);
        this.primitiveType = type.value;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (primitiveType == "int") {
            return new PrimitiveType(Primitives.INT);
        } else if (primitiveType == "bool") {
            return new PrimitiveType(Primitives.BOOL);
        } else if (sTable.containsClass(primitiveType)) {
            return new ObjectType(sTable.getClass(primitiveType));
        } else {
            throw new RuntimeException("Unexpected error: primitive type has to be either int or bool");
        }
    }
}
