/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;

import bsa52_ml2558_yz2369_yh326.ast.node.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.interfc.Interface;
import bsa52_ml2558_yz2369_yh326.ast.node.method.MethodList;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class Class extends Node {

	private Class super_class; // super_class of the current class, might be NULL
    private Identifier id;
	private Interface implemented_itfc; // may not need, since class does not necessarily implement a interface
	// but if it does, the order of the functions in DV must follow that in the interface file, not the class file
	
	private MethodList funcs; // list of member functions
	private VariableList vars; // list of member variables
    
    /**
     * Constructor TODO: need to reimplement this for parsing
     * 	
     * @param line
     * @param col
     * @param id
     */
    public Class(int line, int col, Identifier id) {
        super(line, col, id);
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