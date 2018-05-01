/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for new expression
 */

package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class New extends Expr {
	public New(int line, int col, Identifier id, TypeNode typeNode) {
		super(line, col, id, typeNode);
		// TODO Auto-generated constructor stub
	}

	private Class objClass;
	
	public IRNode translate() {
		// implement using _xi_alloc
		// 1. initialize variable table 
		// 2. initialize dispatch vector
		return null;
	}
}