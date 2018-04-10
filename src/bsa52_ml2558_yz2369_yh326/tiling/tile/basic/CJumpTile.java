package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

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
    protected Assembly generateLocalAssembly() {
        String cjLabel = ((IRCJump)this.root).trueLabel();

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        String ft = freshTemp();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(ft), new AssemblyOperand("0")));
        statements.add(new AssemblyStatement("cmp", new AssemblyOperand(ft), new AssemblyOperand()));
        statements.add(new AssemblyStatement("jne", cjLabel));

        return new Assembly(statements);
    }
}
