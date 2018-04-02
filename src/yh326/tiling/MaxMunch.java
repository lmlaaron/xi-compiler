package yh326.tiling;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.exception.NotTilableException;
import yh326.tiling.tile.*;
import yh326.tiling.tile.binop.arithmetic.AddTile;
import yh326.tiling.tile.binop.arithmetic.SubTile;
import yh326.tiling.tile.binop.comparison.GEQTile;
import yh326.tiling.tile.binop.comparison.GTTile;
import yh326.tiling.tile.binop.comparison.LEQTile;
import yh326.tiling.tile.binop.comparison.LTTile;

import java.io.PrintWriter;
import java.util.*;

public class MaxMunch {
    private static LinkedList<Tile> sortedTiles = new LinkedList<Tile>();

    private static void initTiles() {
        // TODO: there should be some automated way of getting all the tile types.
        // for now, add each one to the list manually

        sortedTiles.add(new AddTile());
        sortedTiles.add(new SubTile());
        sortedTiles.add(new GEQTile());
        sortedTiles.add(new LEQTile());
        sortedTiles.add(new GTTile());
        sortedTiles.add(new LTTile());
        sortedTiles.add(new CJumpTile());
        sortedTiles.add(new CompUnitTile());
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
        sortedTiles.add(new CallTile());


        // sort by size in descending order
        Collections.sort(sortedTiles, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return o1.size() - o2.size();
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
            System.out.println(irRoot.getClass());
            //irRoot.printSExp(new CodeWriterSExpPrinter(new PrintWriter(System.out)));
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
