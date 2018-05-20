package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;

public class Dot extends Expr {
	NodeType leftNodeType = null;
	
    public Dot(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        leftNodeType = children.get(1).typeCheck(sTable);
        if (leftNodeType instanceof ObjectType) {
            Node right = children.get(2);
            if (right instanceof MethodCall) {
                ((MethodCall) right).classOfMethod = ((ObjectType) leftNodeType).getType();
                return right.typeCheck(sTable);
            } else if (right instanceof Identifier) {
                ((Identifier) right).classOfInstance = ((ObjectType) leftNodeType).getType();
                return right.typeCheck(sTable);
            } else {
                throw new MatchTypeException(line, col, "MethodCall or Identifier", leftNodeType);
            }
        } else {
            throw new MatchTypeException(line, col, "Object", leftNodeType);
        }
    }
    
    @Override
    public IRNode translate() {
    		// if it is a member function call
    		if ( children.get(2) instanceof MethodCall ) {
    				return translateCall();
    		}	
    		// it is just a member variable
    		else {
    			return translateVariable(((ObjectType) leftNodeType).getType(), 
    			        (IRExpr) children.get(1).translate(), children.get(2).value);
    		}
    }
    
    private IRNode translateCall() {
		// new offset
		// look up the offset of the called function in java
    	    int funcoffset = -1;
    		if (children.get(2).value == null ) {
    			funcoffset =((ObjectType) leftNodeType).indexOfFunc(((MethodCall) children.get(2)).id.value);
    		} else {
		  funcoffset = ((ObjectType) leftNodeType).indexOfFunc(children.get(2).value);
    		}
		// new argument list
		List<IRExpr> args = new ArrayList<>();
		

		
		// get the name of children.get(1)
		//String children1Name = null;
		IRNode children1IR = children.get(1).translate();

		IRTemp children1IRTemp = null;
		IRStmt children1IRStmt = null;
		if ( children1IR instanceof IRTemp) {
			children1IRTemp = (IRTemp) children1IR; 
		} else if (children1IR instanceof IRESeq) { // children1IR can have side effect
			children1IRStmt = ((IRESeq) children1IR).stmt();
			children1IRTemp = (IRTemp) ((IRESeq) children1IR).expr();
			//children1IRTemp =  ((IRTemp) ((IRESeq) (children.get(1).translate())).expr());
		}
		// children1IRTemp must be resolved and not be null, 
		
		args.add(children1IRTemp);
		args.addAll(
				((IRCall) ((MethodCall) children.get(2)).translate()).args());
		
		// for method calls, interface must be included and the offset can be resolved at compile time
		IRExpr absoluteFuncOffset = new IRConst(funcoffset*8);	
		
		// invoke the member function call
		IRTemp callAddr = new IRTemp(Utilities.freshTemp());
		//return new IRESeq(children1IRStmt, children1IRTemp);
	return new IRESeq(
						new IRSeq(
								children1IRStmt,
								new IRMove(
										callAddr,
										new IRMem(
												new IRBinOp(
														IRBinOp.OpType.ADD,
														// address of DV
														new IRMem(
																children1IRTemp
														),
														absoluteFuncOffset)
												)
										)
								)
				, new IRCall(
						callAddr, 
						args)
				);
    }
    
    public static IRNode translateVariable(XiClass xiClass, IRExpr left, String varName) {
		// new offset
		// look up the offset of the variable in java
		int varoffset = xiClass.indexOfVar(varName);
		
		// resolve the absolute offset
		IRExpr absoluteVarOffset = null;
		if ( xiClass.superClass != null ) {
			absoluteVarOffset = new IRBinOp(
					IRBinOp.OpType.ADD,
					new IRConst(varoffset),
				    new IRTemp("_I_size_"+xiClass.superClass.classId.getId().replace("_", "__"))
					);
		} else {
			absoluteVarOffset = new IRConst(varoffset*8+8);
		}
		
		// return the memory location of the variable
		return new IRMem(
				new IRBinOp(
						IRBinOp.OpType.ADD,
						left,
						absoluteVarOffset
				));
    }

}
