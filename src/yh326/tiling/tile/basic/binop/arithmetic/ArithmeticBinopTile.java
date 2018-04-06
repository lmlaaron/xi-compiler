package yh326.tiling.tile.basic.binop.arithmetic;

import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.basic.binop.BinopTile;

import java.util.LinkedList;

public abstract class ArithmeticBinopTile extends BinopTile {
    /**
     * @return the name of the assembly operator corresponding to validIRBinOpType()
     */
    protected abstract String binOpAssmName();

    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp), new AssemblyOperand()));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
