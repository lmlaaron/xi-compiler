package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;

public class PrimitiveTypeNode extends TypeNode {
    private String primitiveType;

    public PrimitiveTypeNode(int line, int col, String primitiveType) {
        super(line, col, primitiveType);
        this.primitiveType = primitiveType;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (primitiveType == "int") {
            return new VariableType(Primitives.INT);
        } else if (primitiveType == "bool") {
            return new VariableType(Primitives.BOOL);
        } else {
            throw new RuntimeException("Unexpected error: primitive type has to be either int or bool");
        }
    }
}