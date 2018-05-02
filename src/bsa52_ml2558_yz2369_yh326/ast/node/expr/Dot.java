package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;

public class Dot extends Expr {
    public Dot(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType left = children.get(1).typeCheck(sTable);
        if (left instanceof ObjectType) {
            return children.get(2).typeCheck(sTable);
        } else {
            throw new MatchTypeException(line, col, "Object", left);
        }
    }
    
    @Override
    public IRNode translate() {
        return new IRTemp("DOT: TO BE IMPLEMENTED");
    }

}
