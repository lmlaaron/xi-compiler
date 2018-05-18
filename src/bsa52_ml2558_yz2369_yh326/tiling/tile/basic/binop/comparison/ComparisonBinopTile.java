package bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.comparison;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.BinopTile;

public abstract class ComparisonBinopTile extends BinopTile {
    /**
     * @return the name of the assembly conditional jump which occurs when the
     *         'return' value of the binary operator is true
     */
    protected abstract String conditionalJump();

    protected Assembly generateLocalAssembly() {
        String freshTemp = freshTemp();
        String trueLabel = freshLabel();
        String finalLabel = freshLabel();

        String temp1 = freshTemp();
        String temp2 = freshTemp();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();

        statements.add(new AssemblyStatement("mov", new AssemblyOperand(temp1), new AssemblyOperand()));
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(temp2), new AssemblyOperand()));

        statements.add(new AssemblyStatement("cmp", new AssemblyOperand(temp1), new AssemblyOperand(temp2)));
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
