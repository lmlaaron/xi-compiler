package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

import bsa52_ml2558_yz2369_yh326.exception.TileMergeException;

/**
 * Represents a block of assembly instructions, either complete or a fragment
 * introduced by a tile, which needs to be merged with child and parent
 * assemblies.
 * 
 * When Assemblies are merged, it is not simple enough to prepend the lines from
 * the children before the lines of the parents. Often operations in a parent's
 * statement use arguments determined by one or more of the children. This is
 * captured by the filler attribute and merge() function.
 */
public class Assembly {

    final int MAX_PLACEHOLDER_INDEX = 5000; // 5000 arguments shold be enough for signle fucntion
//    /**
//     * Save the mapping of assembly operand to memory location by spilling
//     */
//    StackTable rTable;

    /**
     * All other assembly besides filler
     */
    public LinkedList<AssemblyStatement> statements;
    /**
     * An operand from the current assembly, to be propagated upwards to a
     * placeholder in a parent assembly
     */
    public Optional<AssemblyOperand> filler;

    public Assembly(LinkedList<AssemblyStatement> statements) {
        this.statements = statements;
        this.filler = Optional.empty();
    }

    public Assembly(LinkedList<AssemblyStatement> statements, AssemblyOperand filler) {
        this.statements = statements;
        this.filler = Optional.of(filler);
    }

    public Assembly() {
        statements = new LinkedList<>();
        this.filler = Optional.empty();
    }

    public Assembly(AssemblyOperand filler) {
        statements = new LinkedList<>();
        this.filler = Optional.of(filler);
    }

    /**
     * Fills the first possible placeholder operand with fill
     *
     * @param fill
     *            the 'filler' attribute of a child tile
     *
     * @throws AssertionError
     *             if fill is empty
     * @throws TileMergeException
     *             if there is no place for the filler
     */
    protected void incorporateFiller(AssemblyOperand fill) throws TileMergeException {
        assert !fill.isPlaceholder();

        // find the placeholder with smallest value
        int min = MAX_PLACEHOLDER_INDEX;
        int index = -1;
        int counter = 0;
        for (AssemblyStatement stmt : statements) {
            if (stmt.hasPlaceholder()) {
                // statements.get(index).fillPlaceholder(fill.value());
                // return;
                if (stmt.getPlaceholder().reorderIndex < min) {
                    min = stmt.getPlaceholder().reorderIndex;
                    index = counter;
                }
            }
            counter++;
        }

        // incorporate the filler
        // if ( min < MAX_PLACEHOLDER_INDEX)
        counter = 0;
        for (AssemblyStatement stmt : statements) {
            if (stmt.hasPlaceholder() && counter == index) {
                // stmt.fillPlaceholder(fill.value());
                stmt.fillPlaceholder(fill); // copy all the properties of filler
                return;
            }
            counter++;
        }

        throw new TileMergeException("Assembly can't incorporate filler because there are no empty operands!");
    }

    /**
     * incorporates all fillers and statements from all children into this instance
     *
     * @param childAssemblies
     *            the assemblies of child tiles
     *
     * @throws TileMergeException
     *             if the number of fillers in all children do not match the number
     *             of empty operands in the parent
     */
    public void merge(boolean childrenFirst, Assembly... childAssemblies) throws TileMergeException {
        // get all fillers from child in operands
        for (Assembly child : childAssemblies) {
            if (child.filler.isPresent()) {
                this.incorporateFiller(child.filler.get());
                // operands.add(child.filler.get());
            }
        }
        ArrayList<AssemblyStatement> childStatements = new ArrayList<>();
        Arrays.stream(childAssemblies).forEachOrdered(child -> childStatements.addAll(child.statements));

        if (childrenFirst)
            statements.addAll(0, childStatements);
        else
            statements.addAll(statements.size(), childStatements);

        for (AssemblyStatement stmt : statements)
            if (stmt.hasPlaceholder())
                throw new TileMergeException("Child assemblies did not fill all gaps in parent!");
    }

    /**
     * a unit of assembly is incomplete if any statements have empty placeholder
     * operands, which were should have been filled by a child
     */
    public boolean incomplete() {
        for (AssemblyStatement stmt : statements)
            if (stmt.hasPlaceholder())
                return true;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (AssemblyStatement stmt : statements) {
            s.append(stmt);
            s.append(System.getProperty("line.separator"));
        }
        return s.toString();
    }
}
