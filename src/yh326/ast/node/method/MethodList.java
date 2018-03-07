package yh326.ast.node.method;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.Node;

public class MethodList extends Node {

    /**
     * Constructor
     * @param line
     * @param col
     */
    public MethodList(int line, int col) {
        super(line, col);
    }
    
    /**
     * Constructor
     * @param line
     * @param col
     * @param nodes
     */
    public MethodList(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        loadMethods(sTable);
    }
    
    @Override
    public IRNode translate() {
    		IRNode irNode = new IRCompUnit(fileName);
    		// TODO not finished. Add methods as children
    		return irNode;
    }

}
