package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class ReturnTile extends Tile {
    @Override
    public boolean fits(IRNode root) {

        if (root instanceof IRReturn) {
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.addAll(((IRReturn) root).rets());
            return true;
        }
        else return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        if (this.subtreeRoots != null ) {
        		if ( this.subtreeRoots.size() > 0) {
        			statements.add(new AssemblyStatement("mov",new AssemblyOperand("rax"), new AssemblyOperand()));
        		}
        		String RET1 = freshTemp();
        		if ( this.subtreeRoots.size() > 1) {
        			// TODO tmp fix mov rdx in temp (rdx is used as return value while overloaded before return)
        			statements.add(new AssemblyStatement("mov",new AssemblyOperand(RET1), new AssemblyOperand()));
        		}
        		if ( this.subtreeRoots.size() > 2) {
        	
    				// calulcate the distance between the stack pointer to the return value 
        			for ( int i = 2; i < this.subtreeRoots.size(); i++ ) {
        				AssemblyOperand retOpt = new AssemblyOperand("__RETURN_"+String.valueOf(i));
        				retOpt.type = AssemblyOperand.OperandType.RET_UNRESOLVED;
        				statements.add(new AssemblyStatement("mov", retOpt, new AssemblyOperand()));	
        			}
        		}
        		if ( this.subtreeRoots.size() > 1) {
        			// TODO tmp fix mov rdx in temp (rdx is used as return value while overloaded before return)
        			statements.add(new AssemblyStatement("mov",new AssemblyOperand("rdx"), new AssemblyOperand(RET1)));
        		}
        }
        statements.add(new AssemblyStatement("leave")); // restore the stack before calling
        statements.add(new AssemblyStatement("ret"));
        //TODO: definitely need more than this. consult system V spec

        return new Assembly(statements);
    }
}
