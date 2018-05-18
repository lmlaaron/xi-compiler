package bsa52_ml2558_yz2369_yh326.ast.node.interfc;

import java.util.Set;

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
    public void loadClasses(SymbolTable sTable, Set<String> libPath) throws Exception {
        for (Node child : children) {
            if (child != null && child instanceof InterfaceClass) {
                child.loadClasses(sTable);
            }
        }
    }

    @Override
    public void loadMethods(SymbolTable sTable, Set<String> libPath) throws Exception {
        loadMethods(sTable);
    }
}
