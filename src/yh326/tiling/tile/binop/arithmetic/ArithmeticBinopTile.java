package yh326.tiling.tile.binop.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;
import yh326.tiling.tile.binop.BinopTile;
import yh326.util.NumberGetter;

import java.util.ArrayList;
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
