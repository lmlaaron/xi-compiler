package yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

public class MulTile extends ArithmeticBinopTile {
    public Tile blankClone() {return new AddTile();}

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.MUL;
    }

    protected String binOpAssmName() {
        return "mul";
    }
    
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp), new AssemblyOperand()));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
