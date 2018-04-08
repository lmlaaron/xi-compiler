package yh326.tiling.tile.basic.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class GEQTile extends ComparisonBinopTile {
    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.GEQ;
    }

    @Override
    protected String conditionalJump() {
        return "jge";
    }
}
