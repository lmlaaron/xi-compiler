package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

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
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));

        return new Assembly(statements, new AssemblyOperand("[" + freshTemp + "]"));
    }
}
