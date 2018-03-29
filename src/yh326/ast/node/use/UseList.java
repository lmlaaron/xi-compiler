package yh326.ast.node.use;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;

public class UseList extends Node {
    public UseList(int line, int col) {
        super(line, col);
    }

    public UseList(int line, int col, Use u) {
        super(line, col, u);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // Nothing needs to be checked inside UseList.
        return new UnitType();
    }

    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        for (Node child : children) {
            if (child != null) {
                child.loadMethods(sTable, libPath);
            }
        }
    }

    @Override
    public IRNode translate() {
        return null;
    }

}
