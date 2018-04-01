package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;

import java.util.LinkedList;

public class TempTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRTemp) {
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
    public Tile blankClone() {
        return new TempTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        return new Assembly(
                new AssemblyOperand(
                        ((IRTemp)root).name()
                )
        );
    }
}
