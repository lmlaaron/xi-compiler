package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.ArrayList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public abstract class BinopTile extends Tile {
    public int size() {
        return 1;
    }

    /**
     * set attributes according to validRoot
     *
     * @param validRoot
     *            the IRNode which is being tiled. We have already confirmed that
     *            its type is consistent with validIRBinOpType()
     */
    protected void init(IRBinOp validRoot) {
        root = validRoot;

        subtreeRoots = new ArrayList<>();
        subtreeRoots.add(validRoot.left());
        subtreeRoots.add(validRoot.right());
    }

    /**
     * @return The type of binop that this tile matches
     */
    protected abstract IRBinOp.OpType validIRBinOpType();

    public boolean fits(IRNode irRoot) {
        if (irRoot instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) irRoot;
            if (binOp.opType() == validIRBinOpType()) {
                this.init(binOp);
                return true;
            }
        }
        return false;
    }

    protected abstract Assembly generateLocalAssembly();
}
