package yh326.ast.node.method;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.InsnMapsBuilder;
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
    	    //IRNodeFactory inf = new IRNodeFactory_c();
		    //IRVisitor v = new DummyIRVisitor(inf);
    		IRCompUnit irNode = new IRCompUnit(fileName);
    		// TODO not finished. Add methods as children
    		
    		for (Node child : children) {
    			irNode.appendFunc((IRFuncDecl) child.translate());
    		}
    		return irNode;
    		//return irNode;
    }

}
