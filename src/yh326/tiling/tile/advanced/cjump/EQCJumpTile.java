package yh326.tiling.tile.advanced.cjump;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class EQCJumpTile extends ComparisonCJumpTile {
    @Override
    protected String cjumpOp() {
        return "je";
    }

    @Override
    protected IRBinOp.OpType comparisonType() {
        return IRBinOp.OpType.EQ;
    }

    @Override
    public Tile blankClone() {
        return new EQCJumpTile();
    }
}
