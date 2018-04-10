package bsa52_ml2558_yz2369_yh326.ast.node.literal;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class IntegerLiteral extends ExprAtom {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param value
     */
    public IntegerLiteral(int line, int col, String value) {
        super(line, col, value);
    }

    @Override
    public IRNode translate() {
        return new IRConst(Long.parseLong(this.value));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableType(Primitives.INT);
    }
}
