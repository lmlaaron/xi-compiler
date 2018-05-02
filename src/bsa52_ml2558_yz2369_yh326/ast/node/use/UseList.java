package bsa52_ml2558_yz2369_yh326.ast.node.use;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

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
    public IRNode translate() {
        return null;
    }

}
