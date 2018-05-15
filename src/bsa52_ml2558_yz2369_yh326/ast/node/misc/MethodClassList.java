package bsa52_ml2558_yz2369_yh326.ast.node.misc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
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
            }
        }
        return irNode;
    }

}
