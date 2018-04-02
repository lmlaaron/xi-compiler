package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;

import java.util.LinkedList;

public class JumpTile extends Tile {
    protected String jmplabel;

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRJump) {
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.add(((IRJump) root).target());

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
        return new JumpTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        statements.add(new AssemblyStatement("jmp", new AssemblyOperand()));

        return new Assembly(statements);
    }
}
