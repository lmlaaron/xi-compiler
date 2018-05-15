package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class CallTile extends Tile {
    String targetName;  // if it is not null, then call by label; else call by temp
    //TempTile callTemp;
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCall) {
            this.root = root;
            
            IRCall call = (IRCall) root;
            //subtreeRoots = CallTileUtil.fillCallSubtree(call);
            if (call.target() instanceof IRName) {
            		subtreeRoots = CallTileUtil.fillCallSubtree(call);
                IRName target = (IRName) call.target();
                targetName = target.name();
                return true;
            } else if ( call.target() instanceof IRTemp ) {
            		subtreeRoots.addAll(CallTileUtil.fillCallSubtree(call));
            		subtreeRoots.add(call.target());
            		return true;
            }
            return false;
        } else
            return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
    		if ( targetName != null ) { // call by label
    			LinkedList<AssemblyStatement> statements = new LinkedList<>();
        		CallTileUtil.generateCallAssembly(statements, this.getSubtreeRoots().size(), targetName);
        		return new Assembly(statements);
    		} else { // call by temp
    			LinkedList<AssemblyStatement> statements = new LinkedList<>();
        		CallTileUtil.generateCallAssembly(statements, this.getSubtreeRoots().size(), targetName);
        		return new Assembly(statements);    			
    		}
    }
}
