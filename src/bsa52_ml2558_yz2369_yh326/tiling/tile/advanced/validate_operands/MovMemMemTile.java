package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.validate_operands;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

/**
 * Exists to prevent attempting to move one mem operand to another, which isn't legal
 */
public class MovMemMemTile extends Tile {

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMove) {
            IRMove move = (IRMove)root;
            if (move.source() instanceof IRMem && move.target() instanceof IRMem) {
                IRMem source = (IRMem) move.source();
                IRMem dest = (IRMem) move.target();

                this.subtreeRoots = new LinkedList<>();

                this.subtreeRoots.add(source.expr());
                this.subtreeRoots.add(dest.expr());

                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();

        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), AssemblyOperand.MemWrapped()));
        statements.add(new AssemblyStatement("mov", AssemblyOperand.MemWrapped(), new AssemblyOperand(freshTemp)));

        return new Assembly(statements);
    }
}
