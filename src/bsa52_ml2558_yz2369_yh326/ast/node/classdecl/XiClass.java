/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
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
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class XiClass extends Node {

    // 'global' registry for classes
    public static List<XiClass> all = new LinkedList<XiClass>();
	
	public SymbolTable sVarTable;
	public static int RUNTIME_RESOLVE = -1;
	public XiClass super_class; // super_class of the current class, might be NULL
    public Identifier id;
    public Identifier superClassId;
    private Interface implemented_itfc; // may not need, since class does not necessarily implement a interface
	// but if it does, the order of the functions in DV must follow that in the interface file, not the class file
	
	// below is redundant need to figureout a way to have a Map with indexof method
    public List<String> vars_ordered; // list of member variables
    public List<String> funcs_ordered; // list of member functions
    public Map<String, VariableType> vars; // list of member variables
    public Map<String, Tuple<NodeType, NodeType>> funcs; // list of member functions
    
    public int NumVariables() {
    		if ( super_class == null ) {
    			return vars.size();
    		} else {
    			return vars.size() + super_class.NumVariables();
    		}
    }
    public int NumMethods() {
    			if (implemented_itfc != null ) {
				return implemented_itfc.NumMethods();
			} else if ( super_class == null) {
				return funcs.size();
			} else {
				return funcs.size() + super_class.NumMethods();
			}
    }


    
    /**
     * Constructor TODO: need to reimplement this for parsing
     * 	
     * @param line
     * @param col
     * @param id
     */
    public XiClass(int line, int col, Identifier id) {
        super(line, col, id);
        init(this,line, col, id, null);
    }
    
    public XiClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, id, extend);
        init(this, line, col, id, extend);

    }

    /**
     * Operations common to all constructors
     */
    private static void init(XiClass instance, int line, int col, Identifier id, Identifier extend) {
        instance.id = id;
        instance.super_class = null;
        instance.superClassId = null;
        instance.vars = new HashMap<>();
        instance.funcs = new HashMap<>();
        instance.vars_ordered = new ArrayList<>();
        instance.funcs_ordered = new ArrayList<>();
        instance.sVarTable = new SymbolTable();
        instance.superClassId = extend;

        instance.sVarTable.setCurClass(instance);
        all.add(instance);
    }
    
    @Override
    public void loadClasses(SymbolTable sTable) throws Exception {
        if (sTable.addClass(this) == false)
            throw new AlreadyDefinedException(line, col, id.value);
        if (superClassId != null)
            this.super_class = sTable.getClass(superClassId.value);
        if ( this.super_class!= null && this.super_class.sVarTable != null)
        this.sVarTable.addTable(this.super_class.sVarTable);        
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
            if (child instanceof VarDecl) {
                ((VarDecl) child).typeCheckAndReturn(sTable);
                ((VarDecl) child).typeCheckAndReturn(sVarTable);
                vars_ordered.add(child.value);
            }
        // Second pass process Method
        for (Node child : children) {
            if (child instanceof Method) {
                child.typeCheck(sTable);
                	funcs_ordered.add(child.value);
            }
        }

        sTable.setCurClass(null);
        sTable.exitBlock();
        return new UnitType();
    }

    @Override
    public IRNode translate() {
    	    return null;
    	    // deprecated code, should not be called anyway
    }
    
    public List<IRFuncDecl> listOfIRMethods() {
    		List<IRFuncDecl> list = new ArrayList<>();
    		for (Node child: children) {
    			if ( child instanceof Method) {
    				list.add((IRFuncDecl) child.translate());
    			}
    		}
    		return list;
    }
    
    public int IndexOfVar(String varname) {
    		// if there is no superclass or superclass is fully resolved 
    		// just return the index
    		if ( super_class != null ) {
    			if ( super_class.IndexOfVar(varname)!= RUNTIME_RESOLVE) {
    				return super_class.IndexOfVar(varname);
    			}
    			return super_class.NumVariables()+ vars_ordered.indexOf(varname); 
    		} else {
    			int i=0;
    			return vars_ordered.indexOf(varname);
    		}
    }
    
    public int IndexOfFunc(String funcname) {
		if ( implemented_itfc != null ) {
			return implemented_itfc.IndexOfFunc(funcname);
		} else if ( super_class != null) {
			if ( super_class.IndexOfFunc(funcname)!= RUNTIME_RESOLVE) {
				return super_class.IndexOfFunc(funcname);
			}
			return super_class.NumMethods() + funcs_ordered.indexOf(funcname);
		} else {
			return funcs_ordered.indexOf(funcname);
		}
    }
}