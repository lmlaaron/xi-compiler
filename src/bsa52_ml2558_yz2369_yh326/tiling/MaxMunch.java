package bsa52_ml2558_yz2369_yh326.tiling;

import bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.ExpCallTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.MoveTempCallTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.cjump.*;
import bsa52_ml2558_yz2369_yh326.tiling.tile.advanced.indexing.LEATile;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.*;

import bsa52_ml2558_yz2369_yh326.exception.NotTilableException;
import bsa52_ml2558_yz2369_yh326.tiling.tile.*;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.*;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.AndTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.ARShiftTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.AddTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.DivTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.HMulTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.LShiftTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.ModTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.MulTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.OrTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.RShiftTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.SubTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.arithmetic.XorTile;
import bsa52_ml2558_yz2369_yh326.tiling.tile.basic.binop.comparison.*;

public class MaxMunch {
    private static LinkedList<Tile> sortedTiles = new LinkedList<Tile>();

    private static void initTiles() {
        // TODO: there should be some automated way of getting all the tile types.
        // for now, add each one to the list manually

        sortedTiles.add(new AddTile());
        sortedTiles.add(new SubTile());
        sortedTiles.add(new MulTile());
        sortedTiles.add(new MoveTempCallTile());
        sortedTiles.add(new GEQTile());
        sortedTiles.add(new LEQTile());
        sortedTiles.add(new GTTile());
        sortedTiles.add(new LTTile());
        sortedTiles.add(new CJumpTile());
        sortedTiles.add(new CompUnitTile());
        sortedTiles.add(new AndTile());
        sortedTiles.add(new ARShiftTile());
        sortedTiles.add(new DivTile());
        sortedTiles.add(new HMulTile());
        sortedTiles.add(new LShiftTile());
        sortedTiles.add(new ModTile());
        sortedTiles.add(new OrTile());
        sortedTiles.add(new RShiftTile());
        sortedTiles.add(new XorTile());
        sortedTiles.add(new ConstTile());
        sortedTiles.add(new FuncDeclTile());
        sortedTiles.add(new JumpTile());
        sortedTiles.add(new LabelTile());
        sortedTiles.add(new MemTile());
        sortedTiles.add(new NameTile());
        sortedTiles.add(new ReturnTile());
        sortedTiles.add(new SeqTile());
        sortedTiles.add(new TempTile());
        sortedTiles.add(new MoveTile());
        sortedTiles.add(new ExpCallTile());
        sortedTiles.add(new EQTile());
        sortedTiles.add(new NEQTile());

        sortedTiles.add(new EQCJumpTile());
        sortedTiles.add(new NEQCJumpTile());
        sortedTiles.add(new GTCJumpTile());
        sortedTiles.add(new GEQCJumpTile());
        sortedTiles.add(new LTCJumpTile());
        sortedTiles.add(new LEQCJumpTile());

        sortedTiles.add(new LEATile());

        // sort by size in descending order
        Collections.sort(sortedTiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return o2.size() - o1.size();
            }
        });
    }

    public static Tile munch(IRNode irRoot) throws NotTilableException {
        if (sortedTiles.size() == 0) {
            initTiles();
        }

        // iterate through the tiles. Because they're sorted from biggest to smallest,
        // we keep the value of the first value that fits
        ListIterator<Tile> it = sortedTiles.listIterator();
        Tile tiledRoot = null;
        while (it.hasNext() && tiledRoot == null) {
            Tile next = it.next();
            if (next.fits(irRoot)) {
                tiledRoot = next;
                it.set(tiledRoot.blankClone()); // replace in the list with a copy so values aren't overridden
            }
        }

        if (tiledRoot == null) {
            // TODO: should the detailed printing go inside the exception definition?
            System.out.println("The " + sortedTiles.size() + " tiles we have were not enough to tile this node:");
            System.out.print(irRoot.getClass());
            if (irRoot instanceof IRBinOp) {
                System.out.println(" " + ((IRBinOp) irRoot).opType());
            } else {
                System.out.println();
            }
            // irRoot.printSExp(new CodeWriterSExpPrinter(new PrintWriter(System.out)));
            throw new NotTilableException();
        }

        // now tile subtrees not covered by this tile
        ArrayList<Tile> tiledSubtrees = new ArrayList<>();
        for (IRNode subtreeRoot : tiledRoot.getSubtreeRoots()) {
            tiledSubtrees.add(munch(subtreeRoot));
        }
        tiledRoot.setSubtreeTiles(tiledSubtrees);

        return tiledRoot;
    }
}
