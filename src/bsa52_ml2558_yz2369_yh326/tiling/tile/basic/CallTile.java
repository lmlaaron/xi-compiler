package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class CallTile extends Tile {
	String targetName;
	
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCall) {
            this.root = root;

            IRCall call = (IRCall)root;

            this.subtreeRoots = new LinkedList<>();

            // first add arguments then target MUST follow this order
            subtreeRoots.addAll(call.args());
      
            //subtreeRoots.add(call.target());
            if ( call.target() instanceof IRName) {
            		IRName target = (IRName) call.target();
            		targetName = target.name();
            		return true;
            }
            return false;
        }
        else return false;
    }

    public int retSize() {
		if ( targetName.equals("_xi_out_of_bounds")) {
			return 0;
		} else if ( targetName.equals("_xi_alloc")) {
			return 1;
		}
		try {
    		if (targetName != null) {
    			int index = targetName.lastIndexOf("t");
    			if ( index != -1) {	// assume less than 100 arguments
    				if ( targetName.toCharArray()[(index+1)]== 'p') {
    					return 0;
    				} else if (targetName.toCharArray()[(index+2)]!= 'a' && 
    						targetName.toCharArray()[(index+2)]!= 'b' && 
    						targetName.toCharArray()[(index+2)]!= 'i'  ) {
    						String v = targetName.substring(index+1, index+3);
    						return Integer.parseInt(v);
    				} else {
    					return Integer.parseInt(targetName.substring(index+1,index+2));
    				}
    			}
    			return 0;
    		} else {
    			return 0;
    		}
		} catch (Exception e) {
			return 0;
		}
    }
    
    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();

        // System V calling convention
        
        // check return size, if greater than 2, allocate space on stack first, rcx is reserved for this
		//System.out.print(this.retSize());
        //if (this.retSize() > 2) {
        //		statements.add(new AssemblyStatement("mov", new AssemblyOperand("rcx"), new AssemblyOperand("RETPOINTER")));
        //}
        
        // move first 6 arguments in rdi, rsi, rdx, rcx, r8 and r9.
        int operandNum = this.getSubtreeRoots().size()-1;
        if (operandNum > 0) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdi"), new AssemblyOperand()));
        if (operandNum > 1) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rsi"), new AssemblyOperand()));
        if (operandNum > 2) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdx"), new AssemblyOperand()));
        if (operandNum > 3) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rcx"), new AssemblyOperand()));
        if (operandNum > 4) statements.add(new AssemblyStatement("mov", new AssemblyOperand("r8"), new AssemblyOperand()));
        if (operandNum > 5) statements.add(new AssemblyStatement("mov", new AssemblyOperand("r9"), new AssemblyOperand()));
        
        // PUSH all other arguments onto stack
        for ( int i = this.getSubtreeRoots().size() -1; i > 6; i-- ) {
        		//statements.add( new AssemblyStatement("push", new AssemblyOperand()));
        		statements.add(new AssemblyStatement("mov", new AssemblyOperand("[rsp-"+String.valueOf((this.getSubtreeRoots().size() - 1 -i)*8)+"]"),  new AssemblyOperand()));
        }
        
        //
        statements.add(new AssemblyStatement("call", new AssemblyOperand(targetName)));

        // reduce the size of the sack
        if (operandNum > 6)
        statements.add(new AssemblyStatement("add", new AssemblyOperand("rsp"), new AssemblyOperand(String.valueOf(8*(operandNum - 6)))));
        //statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand("rax")));
        // In IR, CALL Node substitutes as first return value
        // TODO: using function name, test to see if it has return values. If not,
        //      don't have a filler. It could meddle with other tiles in unexpected
        //      ways
        return new Assembly(statements, new AssemblyOperand(/*"_RET0"*/"rax"));
    }
}
