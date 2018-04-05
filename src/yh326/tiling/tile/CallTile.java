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

            //TODO: what to do with args?
            subtreeRoots.addAll(call.args());
            subtreeRoots.add(call.target());
            
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

        // System V calling convention
        // move first 6 arguments in rdi, rsi, rdx, rcx, r8 and r9.
        int operandNum = this.getSubtreeRoots().size()-1;
        if (operandNum > 0) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdi"), new AssemblyOperand()));
        if (operandNum > 1) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rsi"), new AssemblyOperand()));
        if (operandNum > 2) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdx"), new AssemblyOperand()));
        if (operandNum > 3) statements.add(new AssemblyStatement("mov", new AssemblyOperand("rcx"), new AssemblyOperand()));
        if (operandNum > 4) statements.add(new AssemblyStatement("mov", new AssemblyOperand("r8"), new AssemblyOperand()));
        if (operandNum > 5) statements.add(new AssemblyStatement("mov", new AssemblyOperand("r9"), new AssemblyOperand()));
        
        // PUSH all other arguments onto stack
        for ( int i = 6; i < this.getSubtreeRoots().size() -1; i++ ) {
        		statements.add( new AssemblyStatement("push", new AssemblyOperand()));
        }
        // notice that in this case the operands in the assembly have different order compared to the operands in theIR
        statements.add(new AssemblyStatement("call", new AssemblyOperand()));

        
        // In IR, CALL Node substitutes as first return value
        // TODO: using function name, test to see if it has return values. If not,
        //      don't have a filler. It could meddle with other tiles in unexpected
        //      ways
        return new Assembly(statements, new AssemblyOperand("_RET0"));
    }
}
