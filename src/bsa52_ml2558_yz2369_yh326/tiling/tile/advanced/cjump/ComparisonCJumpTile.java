package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.cjump;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

/**
 * Superclass of the set of tiles which efficiently tile cjumps which are conditional
 * on a single comparison operation. We gain efficiency by avoiding an additional
 * conditional jump for setting the value of the comparison and by immediately
 * using the value of said comparison rather than storing it in a temp
 */
public abstract class ComparisonCJumpTile extends Tile {

    protected IRCJump cjump;
    protected IRBinOp comparison;

    /**
     * @return the jump operation which jumps if and only if the result of
     * comparisonType() is true
     */
    protected abstract String cjumpOp();

    /**
     * @return the optype corresponding to the comparison tiled by this instance
     */
    protected abstract IRBinOp.OpType comparisonType();


    @Override
    public boolean fits(IRNode root) {
        /*
        CJUMP( *compare*(x, y) )
         */

        if (root instanceof IRCJump) {
            cjump = (IRCJump) root;
            if (cjump.cond() instanceof IRBinOp) {
                comparison = (IRBinOp) cjump.cond();
                if (comparison.opType() == comparisonType()) {
                    // initialize and add children

                    this.subtreeRoots = new LinkedList<>();
                    subtreeRoots.add(comparison.left());
                    subtreeRoots.add(comparison.right());

                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        /*
        cmp _, _
        cjumpOp() trueLabel
         */
        statements.add(new AssemblyStatement("cmp", new AssemblyOperand(), new AssemblyOperand()));
        statements.add(new AssemblyStatement(cjumpOp(), cjump.trueLabel()));

        return new Assembly(statements);
    }
}
