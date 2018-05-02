/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import java.util.HashMap;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.interfc.Interface;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class XiClass extends Node {

	private XiClass super_class; // super_class of the current class, might be NULL
    public Identifier id;
	private Interface implemented_itfc; // may not need, since class does not necessarily implement a interface
	// but if it does, the order of the functions in DV must follow that in the interface file, not the class file
	
    public Map<String, VariableType> vars; // list of member variables
    public Map<String, Tuple<NodeType, NodeType>> funcs; // list of member functions
    
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
        this.vars = new HashMap<>();
        this.funcs = new HashMap<>();
    }
    
    public XiClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, id, extend);
        this.id = id;
        this.super_class = null;
        this.vars = new HashMap<>();
        this.funcs = new HashMap<>();
    }
    
    @Override
    public void loadClasses(SymbolTable sTable) throws Exception {
        if (sTable.addClass(this) == false) {
            throw new AlreadyDefinedException(line, col, id.value);
        }
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        Utilities.loadClassContent(this, sTable);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.setCurClass(this);
        sTable.enterBlock();
        // First pass only process VarDecl
        for (Node child : children)
            if (child instanceof VarDecl)
                ((VarDecl) child).typeCheckAndReturn(sTable);
        // Second pass process Method
        for (Node child : children) {
            if (child instanceof Method) {
                child.typeCheck(sTable);
            }
        }
        sTable.setCurClass(null);
        sTable.exitBlock();
        return new UnitType();
    }

    @Override
    public IRNode translate() {
        IRCompUnit irNode = new IRCompUnit("_" + id);
        for (Node child : children) {	// only need to translate the function
            if (child instanceof Method) {
                irNode.appendFunc((IRFuncDecl) child.translate());
            }
        }
        return irNode;
    }
}