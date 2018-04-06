package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

public class CJumpTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCJump) {
            IRCJump cj = (IRCJump)root;
            this.root = root;
            subtreeRoots = new LinkedList<>();
            subtreeRoots.add(cj.cond());
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
        return new CJumpTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String cjLabel = ((IRCJump)this.root).trueLabel();

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        statements.add(new AssemblyStatement("cmp", new AssemblyOperand("0"), new AssemblyOperand()));
        statements.add(new AssemblyStatement("jne", cjLabel));

        return new Assembly(statements);
    }
}
