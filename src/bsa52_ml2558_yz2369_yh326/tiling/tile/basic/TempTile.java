package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class TempTile extends Tile {
    IRTemp tmp;

    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRTemp) {
            tmp = (IRTemp) root;
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
            return true;
        } else
            return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        // for _I_size, _I_vt, _I_g_, which are all actually memory locations. This causes them to be treated as
        // registers
        if (Utilities.beginsWith(tmp.name(), "_I_")) {
            String freshTemp = freshTemp();

            LinkedList<AssemblyStatement> statements = new LinkedList<>();
            statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand(tmp.name())));

            return new Assembly(statements, new AssemblyOperand(freshTemp));
        }
        else {
            // don't need to do anything to normal temps
            return new Assembly(new AssemblyOperand(tmp.name(), AssemblyOperand.OperandType.TEMP));
        }
    }
}
