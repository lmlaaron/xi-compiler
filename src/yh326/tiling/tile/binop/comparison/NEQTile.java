package yh326.tiling.tile.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import yh326.tiling.tile.Tile;

public class NEQTile extends ComparisonBinopTile {
    @Override
    protected String conditionalJump() {
        return "jne";
    }

    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.NEQ;
    }

    @Override
    public Tile blankClone() {
        return new NEQTile();
    }
}