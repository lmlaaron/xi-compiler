package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.BinopTile;

public abstract class ArithmeticBinopTile extends BinopTile {
    /**
     * @return the name of the assembly operator corresponding to validIRBinOpType()
     */
    protected abstract String binOpAssmName();

    @Override
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp), new AssemblyOperand()));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
