package bsa52_ml2558_yz2369_yh326.ast.node.misc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.literal.IntegerLiteral;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.AssignSingle;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.AssignToList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class MethodClassList extends Node {
	private Map<String, Long> globalIntSizeMap;
    private SymbolTable localSTable;
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
    public void loadClasses(SymbolTable sTable, Set<String> libPath) throws Exception {
        for (Node child : children) {
            if (child != null && !(child instanceof Method)) {
                child.loadClasses(sTable);
            }
        }
    }
    
    @Override
    public void loadMethods(SymbolTable sTable, Set<String> libPath) throws Exception {
        loadMethods(sTable);
        localSTable = sTable;
    }

    @Override
    public IRNode translate() {
    		this.globalIntSizeMap = new LinkedHashMap<>();
        IRCompUnit irNode = new IRCompUnit("_" + fileName);
        
        // just findout initialized INT const in case it is used for other global vars
        for (Node child: children) {
        		if ( child instanceof AssignSingle) {
        			AssignSingle as = (AssignSingle) child;
        			if (as.getLhs() instanceof VarDecl && 
    					((VarDecl) (as.getLhs())).VarType instanceof PrimitiveType && 
    					((PrimitiveType) ((VarDecl) (as.getLhs())).VarType).getType() == Primitives.INT &&
    					as.getExpr() instanceof IntegerLiteral)  {
        				for ( Identifier id: ((VarDecl) as.getLhs()).ids) {
        					this.globalIntSizeMap.put(Utilities.toIRGlobalName(id.getId(), ((VarDecl) (as.getLhs())).VarType ), Long.parseUnsignedLong(as.getExpr().value));
        					break;
        				}
        			}
        		}
        }
        for (Node child : children) {
            IRNode childIR = child.translate();
            if (childIR instanceof IRFuncDecl) {
                irNode.appendFunc((IRFuncDecl) child.translate());
            } else if (child instanceof XiClass) {
            	irNode.appendVarUninit("_I_size_"+((XiClass) child).classId.getId().replace("_", "__"),   1);
            	irNode.appendVarUninit("_I_vt_"+((XiClass) child).classId.getId().replace("_", "__"),   1);
            	if ( ((XiClass) child).superClass != null ) {
            		irNode.appendVarUninit("_I_size_"+((XiClass) child).superClass.classId.getId().replace("_", "__"),   1);
            		irNode.appendVarUninit("_I_vt_"+((XiClass) child).superClass.classId.getId().replace("_", "__"),   1);
            	}
                for (IRFuncDecl func: ((XiClass) child).GenerateListOfIRMethods(localSTable)) {
            			irNode.appendFunc(func);
                }
            } else if ( child instanceof VarDecl ) {
            		//int unit_size = 1;
            		for (Identifier id: ((VarDecl) child).ids) {
            			irNode.appendVarUninit(Utilities.toIRGlobalName(id.getId(), ((VarDecl) child).VarType),   ((VarDecl) child).getArraySize(this.globalIntSizeMap) );
            		}
            } else if ( child instanceof AssignSingle) {
            		AssignSingle as = (AssignSingle) child;
				for ( Identifier id: ((VarDecl) as.getLhs()).ids) {
            			irNode.appendVarInit(
            					Utilities.toIRGlobalName(id.getId(), ((VarDecl) as.getLhs()).VarType),
            					1, 
            					this.globalIntSizeMap.get(Utilities.toIRGlobalName(id.getId(), ((VarDecl) as.getLhs()).VarType)).intValue());
				}
            } else if ( child instanceof AssignToList ) {
            		// not alllowed for multi assignment definition for global variables
            }
        }
        return irNode;
    }

}
