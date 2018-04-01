package yh326.tiling.tile.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class GEQTile extends ComparisonBinopTile {
    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.GEQ;
    }

    @Override
    protected String conditionalJump() {
        return "jge";
    }

    @Override
    public Tile blankClone() {
        return new GEQTile();
    }
}
