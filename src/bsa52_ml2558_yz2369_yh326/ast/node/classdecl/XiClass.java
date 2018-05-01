/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.interfc.Interface;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.MethodClassList;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class XiClass extends Node {

	private XiClass super_class; // super_class of the current class, might be NULL
    private Identifier id;
	private Interface implemented_itfc; // may not need, since class does not necessarily implement a interface
	// but if it does, the order of the functions in DV must follow that in the interface file, not the class file
	
	private MethodClassList funcs; // list of member functions
	private VariableList vars; // list of member variables
    
    /**
     * Constructor TODO: need to reimplement this for parsing
     * 	
     * @param line
     * @param col
     * @param id
     */
    public XiClass(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
        this.super_class = null;
    }
    
    public XiClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, id, extend);
        this.id = id;
        this.super_class = null;
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        //loadMethods(sTable);
    }

    @Override
    public IRNode translate() {
        IRCompUnit irNode = new IRCompUnit("_" + fileName);
        for (Node child : funcs.children) {	// only need to translate the function
            irNode.appendFunc((IRFuncDecl) child.translate());
        }
        return irNode;
    }
}