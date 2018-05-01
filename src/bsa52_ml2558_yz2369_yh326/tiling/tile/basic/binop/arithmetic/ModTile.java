package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class ModTile extends ArithmeticBinopTile {
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.MOD;
    }

    protected String binOpAssmName() {
        return "idiv";
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        AssemblyOperand srcOpt = new AssemblyOperand();

        // move dividend into RAX
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rax"), srcOpt));
        // IMUL divides dividend RDX:RAX by the given argument, so we need to
        // sign-extend RAX into RDX:
        statements.add(new AssemblyStatement("cqo")); // "convert quad-word to oct-word"
        // now run the div operation
        statements.add(new AssemblyStatement(binOpAssmName(), new AssemblyOperand()));

        // Quotient is in RAX, remainder is in RDX
        Assembly assm = new Assembly(statements, new AssemblyOperand("rdx"));

        return assm;
    }
}