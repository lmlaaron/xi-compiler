package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class MoveTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMove) {
            this.root = root;

            IRMove move = (IRMove) root;

            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.add(move.target());
            subtreeRoots.add(move.source());

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
        LinkedList<AssemblyStatement> statements = new LinkedList<>();

        statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand()));

        return new Assembly(statements);
    }
}
