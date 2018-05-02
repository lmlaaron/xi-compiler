/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for new expression
 */

package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class New extends Expr {
    private Identifier id;
    
	public New(int line, int col, Identifier id) {
		super(line, col, new Keyword(line, col, "new"), id);
		this.id = id;
	}

	private Class objClass;
	
	@Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
	    if (sTable.containsClass(id.value)) {
	        return new ObjectType(sTable.getClass(id.value));
	    } else {
	        throw new OtherException(line, col, id.value + " is not a class.");
	    }
	}
	
	@Override
	public IRNode translate() {
		// implement using _xi_alloc
		// 1. initialize variable table 
		// 2. initialize dispatch vector
		return new IRTemp("NEW: TO BE IMPLEMENTED");
	}
}