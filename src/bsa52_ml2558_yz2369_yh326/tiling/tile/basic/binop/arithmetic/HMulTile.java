package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

import java.util.LinkedList;

public class HMulTile extends ArithmeticBinopTile {
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.HMUL;
    }

    protected String binOpAssmName() {
        return "imul";
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();

        String freshTemp = freshTemp();

        // RAX is one of the operands for the one-arg version of imul
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rax"), new AssemblyOperand()));

        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));

        // perform the operation
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand(freshTemp)));

        // the result is RDX:RAX. we want the high 64 bits, so that's just RDX
        Assembly assm = new Assembly(statements, new AssemblyOperand("rdx"));

        return assm;
    }
}