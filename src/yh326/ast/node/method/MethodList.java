package yh326.ast.node.method;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;

public class MethodList extends Node {

    public MethodList(int line, int col) {
        super(line, col);
    }
    
    public MethodList(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        loadMethods(sTable);
    }

}
