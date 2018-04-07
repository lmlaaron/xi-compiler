package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

public class CompUnitTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCompUnit) {
            this.root = root;
            IRCompUnit cu = (IRCompUnit)root;

            this.subtreeRoots = new LinkedList<>();
            for (IRFuncDecl decl : cu.functions().values())
                subtreeRoots.add(decl);

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
        return new Assembly();
    }
}
