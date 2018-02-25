package yh326.ast.node.use;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;

public class UseList extends Node {
    public UseList(int line, int col, Use u) {
        super(line, col, u);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // Nothing needs to be checked inside UseList.
        return new UnitType();
    }
}
