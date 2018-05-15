package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.CallTileUtil;

public class ExpCallTile extends Tile {
    String targetName;

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRExp && ((IRExp) root).expr() instanceof IRCall) {
            this.root = root;

            IRCall call = (IRCall) ((IRExp) root).expr();
            subtreeRoots = CallTileUtil.fillCallSubtree(call);
            if (call.target() instanceof IRName) {
                IRName target = (IRName) call.target();
                targetName = target.name();
                return true;
            }	else if ( call.target() instanceof IRTemp ) {
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
        return 2;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        CallTileUtil.generateCallAssembly(statements, this.getSubtreeRoots().size(), targetName);
        return new Assembly(statements);
    }
}
