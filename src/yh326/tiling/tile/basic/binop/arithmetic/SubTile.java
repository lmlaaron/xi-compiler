package yh326.tiling.tile.basic.binop.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class SubTile extends ArithmeticBinopTile {
    public Tile blankClone() {return new SubTile();}

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.SUB;
    }

    protected String binOpAssmName() {
        return "sub";
    }
}
