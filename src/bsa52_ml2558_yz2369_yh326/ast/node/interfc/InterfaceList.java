package bsa52_ml2558_yz2369_yh326.ast.node.interfc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;

public class InterfaceList extends Node {
    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param interfc
     */
    public InterfaceList(int line, int col, Interface interfc) {
        super(line, col, interfc);
    }
    
    @Override
    public void loadClasses(SymbolTable sTable, String libPath) throws Exception {
        loadClasses(sTable);
    }

    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        loadMethods(sTable);
    }
}
