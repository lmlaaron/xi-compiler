package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class ConstTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRConst) {
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
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
        // TODO: some operations, including 'cmp', cannot accept a literal as a direct operand.
        //       to compensate for those cases, we allocate a temp to hold the constant value.
        //       however, this is inefficient for cases where we tile operations which do allow
        //       constants

        String constValue = Long.toString(((IRConst)this.root).constant());

        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        statements.add(new AssemblyStatement("mov", freshTemp, constValue));

        return new Assembly(
            statements,
            new AssemblyOperand(
                    freshTemp
            )
        );
    }
}
