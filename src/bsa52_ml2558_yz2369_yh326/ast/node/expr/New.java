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
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
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
		this.objClass = sTable.getClass(id.value);
		if (sTable.containsClass(id.value)) {
	        return new ObjectType(sTable.getClass(id.value));
	    } else {
	        throw new OtherException(line, col, id.value + " is not a class.");
	    }
	}
	
	@Override
	public IRNode translate() {
		// implement using _xi_alloc

		//return new IRTemp("NEW: TO BE IMPLEMENTED");
		List<IRStmt> stmts = new ArrayList<IRStmt>();
		String labelNumber = NumberGetter.uniqueNumberStr();
		String objName = "_I__obj__"+objClass.id+"_"+ labelNumber;
		
		// 1. check the size of _I_size_someClass
		// if _I_size_Point neq zero, initialize the variable
		stmts.add(new IRCJump(
				new IRTemp("_I_size_"+objClass.id),
				objName+"_noinit",
				null));
		
		//and call _I_init_someClass to construct _I_dv_someClass
		stmts.add(new IRExp(
				new IRCall(
						new IRName("_I_init_"+objClass.id))));
		
		// label for no init, directly jump here if _I_size_someClass is not zero
		stmts.add(new IRLabel(objName+"_noinit"));
				
		// 2. initialize variable table 
		stmts.add(
				new IRMove(
						new IRTemp(objName), 
						new IRCall(new IRName("_xi_alloc"), 
								new IRBinOp( IRBinOp.OpType.ADD,
														new IRTemp("_I_size_"+objClass.id), 
														new IRConst(8)))));
		
		// 3. point the DV pointer to _I_dv_someClass
		stmts.add(
				new IRMove(
						new IRMem( new IRTemp(objName)),
						new IRTemp("_I_dv_"+objClass.id)));
		
		return new IRESeq( new IRSeq(stmts), new IRTemp(objName));
	}
}