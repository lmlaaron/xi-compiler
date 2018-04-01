package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyStatement;

import java.util.LinkedList;

public class LabelTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRLabel) {
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
        return new LabelTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String labelName = ((IRLabel)this.root).name();

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        statements.add(new AssemblyStatement(labelName + ":"));
        return new Assembly();
    }
}
