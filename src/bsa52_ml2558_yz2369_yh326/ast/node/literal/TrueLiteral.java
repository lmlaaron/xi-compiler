package bsa52_ml2558_yz2369_yh326.ast.node.literal;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class TrueLiteral extends ExprAtom {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public TrueLiteral(int line, int col) {
        super(line, col, "true");
    }

    @Override
    public IRNode translate() {
        return new IRConst(1);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableType(Primitives.BOOL);
    }
}
