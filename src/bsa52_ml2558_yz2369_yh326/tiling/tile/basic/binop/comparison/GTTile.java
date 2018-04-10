package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class GTTile extends ComparisonBinopTile {
    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.GT;
    }

    @Override
    protected String conditionalJump() {
        return "jg";
    }
}
