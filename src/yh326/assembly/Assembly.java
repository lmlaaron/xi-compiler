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
    public void merge(Assembly... childAssemblies) {
        // incorporate all child fillers
        Arrays.stream(childAssemblies).forEach(
                child -> child.filler.ifPresent(fill -> incorporateFiller(fill))
        );

        // prepend other code from children
        Arrays.stream(childAssemblies).forEach(
                child -> statements.addAll(0, child.statements)
        );
    }
}
