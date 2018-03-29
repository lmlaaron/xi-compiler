package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.List;

public abstract class Tile {
    protected IRNode root;
    protected List<IRNode> subtreeRoots;
    protected List<Tile> subtreeTiles;

    /**
     * If the return value of true, we perform all other necessary 'constructor'
     * operations and this instance is fully initialized
     *
     * @param root
     *            the base of some subtree which is being tiled
     * @return whether the Tile can fit the root
     */
    public abstract boolean fits(IRNode root);

    /**
     * @return the number of IR nodes enclosed by this tile
     */
    public abstract int size();

    /**
     * For our applications we don't need a proper clone, just another uninitialized
     * instance of the same type
     */
    public abstract Tile blankClone();

    /**
     * @return a list of all roots of subtrees which are not a part of this tile
     */
    public List<IRNode> getSubtreeRoots() {
        return subtreeRoots;
    }

    /**
     * informs this instance about the tiling of its children, which may be
     * necessary for assembly code generation.
     *
     * @param subtreeTiles
     *            The tiles rooted at IR nodes returned by this.getSubtreeRoots, in
     *            the same order
     */
    public void setSubtreeTiles(List<Tile> subtreeTiles) {
        this.subtreeTiles = subtreeTiles;
    }

    // TODO: function for generating assembly -- currently we don't have a class for
    // representing assembly!
}
