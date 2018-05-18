package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class Continue extends Stmt {
    private Loop loop;
    
    public Continue(int line, int col) {
        super(line, col, new Keyword(line, col, "continue"));
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        loop = sTable.getLastLoop();
        if (loop == null)
            throw new OtherException(line, col, "Invalid use of continue. Not in any loop.");
        else
            return new UnitType();
    }

    @Override
    public IRNode translate() {
        String labelNumber = loop.labelNumber;
        return new IRJump(new IRName("_head_" + labelNumber));
    }
}
