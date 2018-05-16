package bsa52_ml2558_yz2369_yh326.ast.node.misc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.AssignSingle;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.AssignToList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.StmtList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class MethodClassList extends Node {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public MethodClassList(int line, int col) {
        super(line, col);
    }

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param nodes
     */
    public MethodClassList(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

    @Override
    public void loadClasses(SymbolTable sTable, String libPath) throws Exception {
        loadClasses(sTable);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        loadMethods(sTable);
    }

    @Override
    public IRNode translate() {
        IRCompUnit irNode = new IRCompUnit("_" + fileName);
        for (Node child : children) {
            IRNode childIR = child.translate();
            if (childIR instanceof IRFuncDecl) {
                irNode.appendFunc((IRFuncDecl) child.translate());
            } else if (child instanceof XiClass) {
                for (IRFuncDecl func: ((XiClass) child).GenerateListOfIRMethods()) {
            			irNode.appendFunc(func);
                }
            } else if ( child instanceof VarDecl ) {
            		int unit_size = 1;
            		System.out.println(child.toString());
            		for (Identifier id: ((VarDecl) child).getId()) {
            			System.out.println(id.getId()+ " size "+ String.valueOf(((VarDecl) child).getUnitSize()));
            			irNode.appendVarUninit(Utilities.toIRGlobalName(id.getId(), ((VarDecl) child).VarType), ((VarDecl) child).getUnitSize() );
            		}
            } else if ( child instanceof AssignSingle) {
            		//irNode.appendVarInit(fileName, col, col);
            } else if ( child instanceof AssignToList ) {
            	
            }
        }
        return irNode;
    }

}
