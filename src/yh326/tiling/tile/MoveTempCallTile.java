package yh326.tiling.tile;

import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;

import java.util.LinkedList;

public class MoveTempCallTile extends Tile {	
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRMove) {
            this.root = root;

            IRMove move = (IRMove) root;
            if (move.target() instanceof IRTemp && move.source() instanceof IRCall) {
            		//IRTemp temp =(IRTemp) move.target();
            		IRCall call = (IRCall)move.source();

            		this.subtreeRoots = new LinkedList<>();
            		// first add arguments then target MUST follow this order
            		subtreeRoots.addAll(call.args());
            		subtreeRoots.add(call.target());
            		return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return 3;
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
        for ( int i = this.getSubtreeRoots().size() -1; i >=6; i-- ) {
        		statements.add( new AssemblyStatement("push", new AssemblyOperand()));
        }
        statements.add(new AssemblyStatement("call", new AssemblyOperand()));

        // reduce the size of the sack
        if (operandNum > 6)
        statements.add(new AssemblyStatement("add", new AssemblyOperand("rsp"), new AssemblyOperand(String.valueOf(8*(operandNum - 6)))));
        statements.add(new AssemblyStatement("mov", new AssemblyOperand(((IRTemp) ((IRMove) root).target()).name()), new AssemblyOperand("rax")));
        return new Assembly(statements);
    }
}