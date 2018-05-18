package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class MulTile extends ArithmeticBinopTile {

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.MUL;
    }

    protected String binOpAssmName() {
        return "imul";
    }

//    @Override
//    protected Assembly generateLocalAssembly() {
//        String freshTemp = freshTemp();
//
//        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
//        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
//        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp), new AssemblyOperand()));
//
//        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));
//
//        return assm;
//    }
}
