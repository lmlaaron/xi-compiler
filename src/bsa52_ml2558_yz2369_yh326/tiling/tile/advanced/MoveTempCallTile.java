package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.CallTileUtil;

public class MoveTempCallTile extends Tile {
    String targetName;

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMove) {
            this.root = root;

            IRMove move = (IRMove) root;
            if (move.target() instanceof IRTemp && move.source() instanceof IRCall) {
                // IRTemp temp =(IRTemp) move.target();
                IRCall call = (IRCall) move.source();

                this.subtreeRoots = new LinkedList<>();
                // first add arguments then target MUST follow this order
                subtreeRoots.addAll(call.args());
                // subtreeRoots.add(call.target());
                if (call.target() instanceof IRName) {
                    IRName target = (IRName) call.target();
                    targetName = target.name();
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        CallTileUtil.generateCallAssembly(statements, this.getSubtreeRoots().size(), targetName);
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(((IRTemp) ((IRMove) root).target()).name()),
                new AssemblyOperand("rax")));
        return new Assembly(statements);
    }
}