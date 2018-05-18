package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;

public abstract class TypeNode extends Node {

    public TypeNode(int line, int col, String string) {
        super(line, col, string);
    }

    public TypeNode(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

    public abstract NodeType typeCheckSkipSize(SymbolTable sTable) throws Exception;
}
