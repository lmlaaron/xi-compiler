package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class NameTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRName) {
            this.root = root;
            this.subtreeRoots = new LinkedList<IRNode>();
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
        LinkedList<AssemblyStatement> statements = new LinkedList<>();
       // statements.add(new AssemblyStatement("lea ",,));
      //  statements.add(e);
       // return new Assembly(statements);
 //   	return new Assembly(new AssemblyOperand(  "["+ (((IRName) root).name())+"+rip]"       )      );
      	return new Assembly(new AssemblyOperand(   (((IRName) root).name())     )      );
    }
}
