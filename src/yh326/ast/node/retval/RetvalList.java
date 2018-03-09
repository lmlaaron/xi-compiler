package yh326.ast.node.retval;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;

/**
 * A node that represents a list of return value types.
 * @author Syugen
 *
 */
public class RetvalList extends Node {
    public RetvalList(int line, int col, TypeNode t) {
        super(line, col, t);
    }
    
    public RetvalList(int line, int col) {
        super(line, col);
    }
    
    @Override
    public IRNode translate() {
    	List<IRExpr> exprs = new ArrayList<IRExpr> ();
    	for (Node child : children) {
    		exprs.add((IRExpr) child.translate());
    	}
    	return new IRReturn(exprs);
    }
}
