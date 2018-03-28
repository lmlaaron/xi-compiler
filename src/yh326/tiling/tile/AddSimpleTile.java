package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.ArrayList;

public class AddSimpleTile extends Tile {
    public Tile blankClone() {
        AddSimpleTile clone = new AddSimpleTile();
        return clone;
    }

    public int size() { return 1; }

    /**
     * set attributes according to validRoot
     *
     * @param validRoot the IRNode which is being tiled
     */
    private void init(IRBinOp validRoot) {
        root = validRoot;

        subtreeRoots = new ArrayList<>();
        subtreeRoots.add(validRoot.left());
        subtreeRoots.add(validRoot.right());
    }

    public boolean fits(IRNode irRoot) {
        if (irRoot instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) irRoot;
            if (binOp.opType() == IRBinOp.OpType.ADD) {
                this.init(binOp);
                return true;
            }
        }
        return false;
    }
}
