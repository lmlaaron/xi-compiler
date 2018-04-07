package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

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
