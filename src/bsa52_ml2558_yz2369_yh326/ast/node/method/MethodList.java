package bsa52_ml2558_yz2369_yh326.ast.node.method;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class MethodList extends Node {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public MethodList(int line, int col) {
        super(line, col);
    }

    /**
     * Constructor
     * 
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
        IRCompUnit irNode = new IRCompUnit("_" + fileName);
        for (Node child : children) {
            irNode.appendFunc((IRFuncDecl) child.translate());
        }
        return irNode;
    }

}