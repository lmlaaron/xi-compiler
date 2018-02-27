package yh326.ast.node.interfc;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;

public class InterfaceList extends Node {
    public InterfaceList(int line, int col, Interface interfc) {
        super(line, col, interfc);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        loadMethods(sTable);
    }
}
