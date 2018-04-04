package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import yh326.assembly.Assembly;

import java.util.LinkedList;

public class SeqTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRSeq) {
            this.root = root;

            IRSeq seq = (IRSeq)root;

            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.addAll(seq.stmts());

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
        return new SeqTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        return new Assembly();
    }
}