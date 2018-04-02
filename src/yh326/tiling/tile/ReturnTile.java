package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyStatement;

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
        statements.add(new AssemblyStatement("ret"));
        //TODO: definitely need more than this. consult system V spec

        return new Assembly(statements);
    }
}
