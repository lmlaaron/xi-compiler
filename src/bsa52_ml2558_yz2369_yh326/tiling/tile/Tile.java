package bsa52_ml2558_yz2369_yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.exception.TileMergeException;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;

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
     * most tiles want the assembly for their children to go first. There are some
     * exceptions to this, including function declarations (the label should go
     * before the code)
     */
    protected boolean childAssmGoesFirst() {
        return true;
    }

    /**
     * @return the number of IR nodes enclosed by this tile
     */
    public abstract int size();

    /**
     * For our applications we don't need a proper clone, just another uninitialized
     * instance of the same type
     */
    public Tile blankClone() {
        for (Constructor<?> constructor : this.getClass().getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                try {
                    Object o = constructor.newInstance();
                    if (o instanceof Tile) {
                        return (Tile) o;
                    }
                } catch (Exception e) {
                    // shouldn't ever happen
                    throw new RuntimeException(e);
                }
            }
        }
        // shouldn't ever get here:
        throw new RuntimeException("No constructors accepted zero arguments!");
    }

    /**
     * @return a list of all roots of subtrees which are not a part of this tile
     */
    public List<IRNode> getSubtreeRoots() {
        if (subtreeRoots == null) {
            return new LinkedList<>();
        } else {
            return subtreeRoots;
        }
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

    /**
     * @return the Assembly containing instructions local to this object, not
     *         children or parents
     */
    protected abstract Assembly generateLocalAssembly();

    /**
     * @return the Assembly containing instructions belonging to this tile and all
     *         children
     */
    public Assembly generateAssembly() {
        Assembly[] childAssm = new Assembly[subtreeTiles.size()];
        for (int i = 0; i < childAssm.length; i++)
            childAssm[i] = subtreeTiles.get(i).generateAssembly();

        Assembly localAssm = generateLocalAssembly();

        if (Settings.asmComments) {
            // for debugging, add a comment for each tile to see how code was generated
            AssemblyStatement[] comment = AssemblyStatement.comment(this.getClass().getSimpleName().toString());
            for (int i = 0; i < comment.length; i++) {
                localAssm.statements.addFirst(comment[i]);
            }
        }

        // for debugging only
        StringBuilder localStatements = new StringBuilder();
        localAssm.statements.stream().forEachOrdered(stmt -> localStatements.append(stmt.toString() + "\n"));

        try {
            localAssm.merge(childAssmGoesFirst(), childAssm);
        } catch (TileMergeException e) {
            System.out.println("Issue merging parent tile " + this.getClass().getSimpleName() + " with child tiles: ");
            for (Tile child : subtreeTiles)
                System.out.println(child.getClass().getSimpleName());
            System.out.println(
                    "Hint: do the number of 'empty' arguments in the parent match the sum of the fillers in the children?");
            System.exit(1);
        }

        if (localAssm.incomplete()) {
            System.out.println("============================");
            System.out.println("Incomplete code after merge!");
            System.out.println();
            System.out.println("Local Assembly:");
            System.out.println(localStatements);
            System.out.println();
            System.out.println("Subtree code:");
            for (int i = 0; i < childAssm.length; i++) {
                System.out.println();
                System.out.println(i + " - has filler: " + childAssm[i].filler.isPresent());
                System.out.println(childAssm[i]);
            }
            System.out.println("============================");
        }

        return localAssm;
    }

    protected String freshTemp() {
        return "__FreshTemp_" + NumberGetter.uniqueNumber();
    }

    protected String freshLabel() {
        return "__FreshLabel_" + NumberGetter.uniqueNumber();
    }
}
