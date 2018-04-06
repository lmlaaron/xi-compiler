package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

import yh326.assembly.AssemblyOperand;
import java.util.LinkedList;

public class ReturnTile extends Tile {
    @Override
    public boolean fits(IRNode root) {

        if (root instanceof IRReturn) {
            this.root = root;
            return true;
        }
        else return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Tile blankClone() {
        return new ReturnTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        if (this.subtreeRoots != null && this.subtreeRoots.size() > 0) {
        		statements.add(new AssemblyStatement("mov",new AssemblyOperand("rax"), new AssemblyOperand()));
        }
        statements.add(new AssemblyStatement("leave")); // restore the stack before calling
        statements.add(new AssemblyStatement("ret"));
        //TODO: definitely need more than this. consult system V spec

        return new Assembly(statements);
    }
}
