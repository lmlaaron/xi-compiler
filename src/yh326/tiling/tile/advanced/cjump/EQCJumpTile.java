package yh326.tiling.tile.advanced.cjump;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class EQCJumpTile extends ComparisonCJumpTile {
    @Override
    protected String cjumpOp() {
        return "je";
    }

    @Override
    protected IRBinOp.OpType comparisonType() {
        return IRBinOp.OpType.EQ;
    }
}
