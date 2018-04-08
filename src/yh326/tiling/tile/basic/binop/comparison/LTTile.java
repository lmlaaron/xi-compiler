package yh326.tiling.tile.basic.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class LTTile extends  ComparisonBinopTile {
    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.LT;
    }

    @Override
    protected String conditionalJump() {
        return "jl";
    }
}
