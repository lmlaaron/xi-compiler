package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;

public class NonArrayTypeNode extends TypeNode {
    private String type;

    public NonArrayTypeNode(int line, int col, String type) {
        super(line, col, type);
        this.type = type;
    }
    
    public NonArrayTypeNode(int line, int col, Identifier type) {
        super(line, col, type.value);
        this.type = type.value;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (type == "int") {
            return new PrimitiveType(Primitives.INT);
        } else if (type == "bool") {
            return new PrimitiveType(Primitives.BOOL);
        } else if (sTable.containsClass(type)) {
            return new ObjectType(sTable.getClass(type));
        } else {
            throw new RuntimeException("Unexpected error: primitive type has to be either int or bool");
        }
    }
    
    @Override
    public NodeType typeCheckSkipSize(SymbolTable sTable) throws Exception {
        return typeCheck(sTable);
    }
}
