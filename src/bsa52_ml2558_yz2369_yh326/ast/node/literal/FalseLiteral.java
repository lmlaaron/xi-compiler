package bsa52_ml2558_yz2369_yh326.ast.node.literal;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class FalseLiteral extends ExprAtom {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public FalseLiteral(int line, int col) {
        super(line, col, "false");
    }

    @Override
    public IRNode translate() {
        return new IRConst(0);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new PrimitiveType(Primitives.BOOL);
    }
}
