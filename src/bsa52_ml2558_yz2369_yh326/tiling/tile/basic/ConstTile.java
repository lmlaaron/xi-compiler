package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class ConstTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRConst) {
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
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
        String constValue = Long.toString(((IRConst)this.root).constant());
        return new Assembly(
            new AssemblyOperand(
                    constValue
            )
        );
    }
}
