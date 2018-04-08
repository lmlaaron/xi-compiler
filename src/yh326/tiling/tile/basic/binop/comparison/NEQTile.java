package yh326.tiling.tile.basic.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;

public class NEQTile extends ComparisonBinopTile {
    @Override
    protected String conditionalJump() {
        return "jne";
    }

    @Override
    protected IRBinOp.OpType validIRBinOpType() {
        return IRBinOp.OpType.NEQ;
    }
}