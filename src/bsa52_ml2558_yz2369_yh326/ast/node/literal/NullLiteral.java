package bsa52_ml2558_yz2369_yh326.ast.node.literal;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class NullLiteral extends ExprAtom {

    public NullLiteral(int line, int col) {
        super(line, col, "null");
        // TODO Auto-generated constructor stub
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new PrimitiveType(Primitives.ANY);
    }
    
    @Override
    public IRNode translate() {
        return new IRConst(0);
    }
}
