package yh326.tiling.tile.basic.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class LEQTile extends  ComparisonBinopTile {
    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.LEQ;
    }

    @Override
    protected String conditionalJump() {
        return "jle";
    }
}
