package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
public class Dot extends Expr {
	NodeType leftNodeType;
	
    public Dot(int line, int col, Node... nodes) {
        super(line, col, nodes);
        leftNodeType= null;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        leftNodeType = children.get(1).typeCheck(sTable);
        if (leftNodeType instanceof ObjectType) {
            Node right = children.get(2);
            if (right instanceof MethodCall) {
                ((MethodCall) right).setClassOfMethod(((ObjectType) leftNodeType).getType());
                return right.typeCheck(sTable);
            } else if (right instanceof Identifier) {
                ((Identifier) right).setClassOfInstance(((ObjectType) leftNodeType).getType());
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
    			// new offset
    			// look up the offset of the called function in java
    			int funcoffset = ((ObjectType) leftNodeType).IndexOfFunc(children.get(2).value);
    			
    			// new argument list
    			List<IRExpr> args = new ArrayList<>();
    			args.add((IRExpr) children.get(1).translate());
    			
    			args.addAll(
    					((IRCall) ((MethodCall) children.get(2)).translate()).args());
    			
    			// invoke the member function call
    			return new IRCall(
    					// calculate the address of the called function
    					new IRBinOp(
    							IRBinOp.OpType.ADD,
    							// address of DV
    							new IRMem(
    									new IRMem(
    											((IRTemp) ((IRESeq) (children.get(1).translate())).expr())
    													)
    							),
    							// offset
    							new IRConst(funcoffset)
    					),
    					// arguments list of the called function
    					args
    				);
    		}	
    		// it is just a member variable
    		else {
    			// new offset
    			// look up the offset of the variable in java
    			int varoffset = ((ObjectType) leftNodeType).IndexOfVar(children.get(2).value);
    			
    			// return the memory location of the variable
    			return new IRMem(
    					new IRBinOp(
    							IRBinOp.OpType.ADD,
    							(IRExpr) children.get(1).translate(),
    							new IRConst(varoffset)
    					));
    		}
    }

}
