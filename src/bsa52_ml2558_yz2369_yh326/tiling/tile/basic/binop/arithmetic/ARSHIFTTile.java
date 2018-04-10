package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic;

import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class ARSHIFTTile extends ArithmeticBinopTile {
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.ARSHIFT;
    }

    protected String binOpAssmName() {
        return "sar";
    }
}