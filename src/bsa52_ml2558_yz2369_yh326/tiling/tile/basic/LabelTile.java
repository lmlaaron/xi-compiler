package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class LabelTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRLabel) {
            this.root = root;
            this.subtreeRoots = new LinkedList<>();
            return true;
        } else
            return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        String labelName = ((IRLabel) this.root).name();

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        AssemblyStatement labelStmt = new AssemblyStatement(labelName + ":");
        labelStmt.isFunctionLabel = true; // for printing reason, need to distinguish label and other statements
        statements.add(labelStmt);
        return new Assembly(statements);
    }
}
