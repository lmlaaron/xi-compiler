package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.util.NumberGetter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

public class AddSimpleTile extends Tile {
    public Tile blankClone() {
        AddSimpleTile clone = new AddSimpleTile();
        return clone;
    }

    public int size() {
        return 1;
    }

    /**
     * set attributes according to validRoot
     *
     * @param validRoot
     *            the IRNode which is being tiled
     */
    private void init(IRBinOp validRoot) {
        root = validRoot;

        subtreeRoots = new ArrayList<>();
        subtreeRoots.add(validRoot.left());
        subtreeRoots.add(validRoot.right());
    }

    public boolean fits(IRNode irRoot) {
        if (irRoot instanceof IRBinOp) {
            IRBinOp binOp = (IRBinOp) irRoot;
            if (binOp.opType() == IRBinOp.OpType.ADD) {
                this.init(binOp);
                return true;
            }
        }
        return false;
    }

    protected Assembly generateLocalAssembly() {
        String freshTemp = "__FreshTemp_" + NumberGetter.uniqueNumber();

        LinkedList<AssemblyStatement> statements = new LinkedList<AssemblyStatement>();
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
        statements.add(new AssemblyStatement("add", new AssemblyOperand(freshTemp), new AssemblyOperand()));

        Assembly assm = new Assembly(statements, new AssemblyOperand(freshTemp));

        return assm;
    }
}
