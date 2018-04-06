package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

public class NameTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRName) {
            this.root = root;
            this.subtreeRoots = new LinkedList<IRNode>();
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
        return new NameTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        return new Assembly(
                new AssemblyOperand( ((IRName)root).name() )
        );
    }
}
