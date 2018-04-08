package yh326.tiling.tile.basic.binop.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class LSHIFTTile extends ArithmeticBinopTile {
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.LSHIFT;
    }

    protected String binOpAssmName() {
        return "shl";
    }
}