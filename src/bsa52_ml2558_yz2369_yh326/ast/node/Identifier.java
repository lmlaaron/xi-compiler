package bsa52_ml2558_yz2369_yh326.ast.node;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.exception.NotDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class Identifier extends Expr {
    private String id;

    public Identifier(int line, int col, String id) {
        super(line, col, id);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType type = sTable.getVariableType(id);
        if (type != null) {
            return type;
        } else if (sTable.getFunctionType(id) != null) {
            throw new OtherException(line, col, id + " is not a variable");
        } else {
            throw new NotDefinedException(line, col, id);
        }
    }

    @Override
    public IRNode translate() {
        return new IRTemp(id);
    }
}
