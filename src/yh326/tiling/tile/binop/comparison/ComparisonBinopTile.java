package yh326.tiling.tile.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class ComparisonBinopTile extends Tile {
    public int size() {
        return 1;
    }

    /**
     * set attributes according to validRoot
     *
     * @param validRoot
     *            the IRNode which is being tiled. We have already confirmed
     *            that its type is consistent with validIRBinOpType()
     */
    protected void init(IRBinOp validRoot) {
        root = validRoot;

        subtreeRoots = new ArrayList<>();
        subtreeRoots.add(validRoot.left());
        subtreeRoots.add(validRoot.right());
    }

    /**
     * @return The type of binop that this tile matches
     */
    protected abstract IRBinOp.OpType validIRBinOpType();

    /**
     * @return the name of the assembly conditional jump which occurs when
     *          the 'return' value of the binary operator is true
     */
    protected abstract String conditionalJump();

    public boolean fits(IRNode irRoot) {
        if (irRoot instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) irRoot;
            if (binOp.opType() == validIRBinOpType()) {
                this.init(binOp);
                return true;
            }
        }
        return false;
    }

    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();
        String trueLabel = freshLabel();
        String finalLabel = freshLabel();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("cmp", new AssemblyOperand(), new AssemblyOperand()));
        statements.add(new AssemblyStatement(conditionalJump(), trueLabel));
        statements.add(new AssemblyStatement("mov", freshTemp, "0"));
        statements.add(new AssemblyStatement("jmp", finalLabel));
        statements.add(new AssemblyStatement(trueLabel + ":"));
        statements.add(new AssemblyStatement("mov", freshTemp, "1"));
        statements.add(new AssemblyStatement(finalLabel + ":"));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
