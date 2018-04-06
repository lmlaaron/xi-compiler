package yh326.tiling.tile.binop.comparison;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;
import yh326.tiling.tile.binop.BinopTile;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class ComparisonBinopTile extends BinopTile {
    /**
     * @return the name of the assembly conditional jump which occurs when
     *          the 'return' value of the binary operator is true
     */
    protected abstract String conditionalJump();

    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();
        String trueLabel = freshLabel();
        String finalLabel = freshLabel();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("cmp", new AssemblyOperand(), new AssemblyOperand()));
        statements.add(new AssemblyStatement(conditionalJump(), trueLabel));
        statements.add(new AssemblyStatement("mov", freshTemp, "0"));
        statements.add(new AssemblyStatement("jmp", finalLabel));
        AssemblyStatement tl = new AssemblyStatement(trueLabel + ":");
        tl.isOtherLabel = true;
        statements.add(tl);
        statements.add(new AssemblyStatement("mov", freshTemp, "1"));
        AssemblyStatement fl = new AssemblyStatement(finalLabel + ":");
        fl.isOtherLabel = true;
        statements.add(fl);

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
