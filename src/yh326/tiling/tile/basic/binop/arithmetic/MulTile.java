package yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

public class MulTile extends ArithmeticBinopTile {

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.MUL;
    }

    protected String binOpAssmName() {
        return "mul";
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        //TODO: eax contains the low half of the result,

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rax"), new AssemblyOperand()));
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand()));
        statements.add(new AssemblyStatement("mov", freshTemp, "rax"));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
