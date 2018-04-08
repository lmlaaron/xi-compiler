package yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

public class DIVTile extends ArithmeticBinopTile {

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.DIV;
    }

    protected String binOpAssmName() {
        return "idiv";
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();

        //TODO: implementing 64 bit div is tricky. This probably doesn't work in all cases...

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        AssemblyOperand srcOpt =new AssemblyOperand();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rax"), srcOpt));
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp), new AssemblyOperand()));
        statements.add(new AssemblyStatement("mov", srcOpt, new AssemblyOperand("rax")));
            
        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
