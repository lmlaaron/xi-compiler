package yh326.tiling.tile.basic.binop.arithmetic;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class AddTile extends ArithmeticBinopTile {
    public Tile blankClone() {return new AddTile();}

    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.ADD;
    }

    protected String binOpAssmName() {
        return "add";
    }
}
