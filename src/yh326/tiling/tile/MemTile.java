package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;

import java.util.LinkedList;

public class MemTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        boolean doesFit = root instanceof IRMem;

        if (doesFit) {
            this.root = root;

            this.subtreeRoots = new LinkedList<>();
            this.subtreeRoots.add(((IRMem) root).expr());
        }

        return doesFit;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Tile blankClone() {
        return new MemTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));

        return new Assembly(statements, new AssemblyOperand("[" + freshTemp + "]"));
    }
}
