package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

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
    public Tile blankClone() {
        return new MoveTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();

        statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand()));

        return new Assembly(statements);
    }
}
