/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for new expression
 */

package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class New extends Expr {
    private Identifier id;
    
	public New(int line, int col, Identifier id) {
		super(line, col, new Keyword(line, col, "new"), id);
		this.id = id;
	}

	private XiClass objClass;
	
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
		/*List<IRStmt> stmts = new ArrayList<IRStmt>();
		String labelNumber = NumberGetter.uniqueNumberStr();
		stmts.add(
				new IRMove(
						new IRTemp("_I_obj_"+objClass.value+"_"+ labelNumber), 
						new IRCall(new IRName("_xi_alloc"), 
								new IRBinOp( IRBinOp.OpType.ADD,
														new IRTemp("_I_size_"+objClass.value), 
														new IRConst(8)))));
		
		stmts.add(
				new IRMove(
						new IRMem(new IRTemp("_I_obj_"+objClass.value+"_"+ labelNumber)),
														new IRTemp("_I_vt_"+objClass.value)));

		*/
		
		
	}
}