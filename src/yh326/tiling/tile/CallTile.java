package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;

import java.util.LinkedList;

public class CallTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRCall) {
            this.root = root;

            IRCall call = (IRCall)root;

            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.add(call.target());
            //TODO: what to do with args?

            return true;
        }
        else return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Tile blankClone() {
        return new CallTile();
    }

    @Override
    protected Assembly generateLocalAssembly() {
        LinkedList<AssemblyStatement> statements = new LinkedList<>();

        //TODO: THIS IS DEFINITELY NOT CORRECT, SEE SYSTEM V FOR DETAILS
        statements.add(new AssemblyStatement("call", new AssemblyOperand()));


        // In IR, CALL Node substitutes as first return value
        // TODO: using function name, test to see if it has return values. If not,
        //      don't have a filler. It could meddle with other tiles in unexpected
        //      ways
        return new Assembly(statements, new AssemblyOperand("_RET0"));
    }
}
