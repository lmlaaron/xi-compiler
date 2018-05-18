package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class MoveTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMove) {
            this.root = root;

            IRMove move = (IRMove) root;

            this.subtreeRoots = new LinkedList<>();
            // mind the order of source and target
            // in the generated assembly
            // we have
            // mov temp, src
            // mov des, temp
            // thus have to add source first then target
            if (((IRMove) root).target() instanceof IRMem && ((IRMove) root).source() instanceof IRMem) {
                subtreeRoots.add(move.source());
                subtreeRoots.add(move.target());
            } else if ( ((IRMove) root).source() instanceof  IRName && (!((IRName)((IRMove) root).source()).label().startsWith("_I_g_")) ) {
                subtreeRoots.add(move.source());
                subtreeRoots.add(move.target());
            } else {
                subtreeRoots.add(move.target());
                subtreeRoots.add(move.source());
            }

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
        if (((IRMove) root).target() instanceof IRMem && ((IRMove) root).source() instanceof IRMem) {
            String freshTemp = freshTemp();
            statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), new AssemblyOperand()));
            statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand(freshTemp)));
        }  else if ( ((IRMove) root).source() instanceof  IRName && (!((IRName)((IRMove) root).source()).label().startsWith("_I_g_")) ) {
            String freshTemp = freshTemp();
        	statements.add(new AssemblyStatement("lea", new AssemblyOperand(freshTemp), new AssemblyOperand()));
            statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand(freshTemp)));
        } else {
            statements.add(new AssemblyStatement("mov", new AssemblyOperand(), new AssemblyOperand()));
        }

        return new Assembly(statements);
    }
}
