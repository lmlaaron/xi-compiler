package yh326.assembly;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;


/**
Represents a block of assembly instructions, either complete or a fragment
introduced by a tile, which needs to be merged with child and parent assemblies.

When Assemblies are merged, it is not simple enough to prepend the lines from the
children before the lines of the parents. Often operations in a parent's statement
use arguments determined by one or more of the children. This is captured by the
filler attribute and merge() function.
 */
public class Assembly {
    /**
     * All other assembly besides filler
     */
    public LinkedList<AssemblyStatement> statements;
    /**
     * An operand from the current assembly, to be propagated upwards to
     * a placeholder in a parent assembly
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
     * @param fill the 'filler' attribute of a child tile
     *
     * @throws AssertionError if fill is empty
     */
    protected void incorporateFiller(AssemblyOperand fill) {
        assert !fill.isPlaceholder();
        for (AssemblyStatement stmt : statements) {
            if (stmt.hasPlaceholder())
                stmt.fillPlaceholder(fill.value());
        }
    }

    /**
     * incorporates all fillers and statements from all children into
     * this instance
     *
     * @param childAssemblies the assemblies of child tiles
     */
    public void merge(boolean childrenFirst, Assembly... childAssemblies) {
        // incorporate all child fillers
        Arrays.stream(childAssemblies).forEachOrdered(
                child -> child.filler.ifPresent(fill -> incorporateFiller(fill))
        );

        ArrayList<AssemblyStatement> childStatements = new ArrayList<>();
        Arrays.stream(childAssemblies).forEachOrdered(
                child -> childStatements.addAll(child.statements)
        );

        if (childrenFirst)
            statements.addAll(0, childStatements);
        else
            statements.addAll(statements.size(), childStatements);

    }

    /**
     * a unit of assembly is incomplete if any statements have empty
     * placeholder operands, which were should have been filled by a child
     */
    public boolean incomplete() {
        for (AssemblyStatement stmt : statements)
            if (stmt.hasPlaceholder()) return true;
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
