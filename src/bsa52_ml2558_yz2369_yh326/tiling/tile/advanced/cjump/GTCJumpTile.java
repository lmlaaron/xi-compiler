package bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.cjump;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class GTCJumpTile extends ComparisonCJumpTile {
    @Override
    protected String cjumpOp() {
        return "jg";
    }

    @Override
    protected IRBinOp.OpType comparisonType() {
        return IRBinOp.OpType.GT;
    }
}
