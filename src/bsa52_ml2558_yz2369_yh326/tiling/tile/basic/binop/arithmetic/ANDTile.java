package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class AndTile extends ArithmeticBinopTile {
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.AND;
    }

    protected String binOpAssmName() {
        return "and";
    }
}