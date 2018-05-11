package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;

public class Break extends Stmt {
    private Stmt loop;

    public Break(int line, int col) {
        super(line, col, new Keyword(line, col, "break"));
        // TODO Auto-generated constructor stub
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        loop = sTable.getLastLoop();
        if (loop == null)
            throw new OtherException(line, col, "Invalid use of break. Not in any loop.");
        else
            return new UnitType();
    }
}
