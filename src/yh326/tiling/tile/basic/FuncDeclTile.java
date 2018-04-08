package yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.assembly.AssemblyOperand;
import yh326.assembly.AssemblyStatement;
import yh326.tiling.tile.Tile;

import java.util.LinkedList;

public class FuncDeclTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRFuncDecl) {
            this.root = root;

            IRFuncDecl decl = (IRFuncDecl)root;


            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.add(decl.body());

            return true;
        }
        else return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        IRFuncDecl decl = (IRFuncDecl)root;

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        // assembly annotations for function
        statements.add(new AssemblyStatement(".text"));
        statements.add(new AssemblyStatement(".globl " + decl.name() ));
        statements.add(new AssemblyStatement(".type " + decl.name() + ", @function"));
        statements.add(new AssemblyStatement(decl.name() + ":", true));
        statements.add(new AssemblyStatement("push", "rbp"));
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rbp"), new AssemblyOperand("rsp")));
        statements.add(new AssemblyStatement("sub", new AssemblyOperand("rsp"), new AssemblyOperand("STACKSIZE")));      
        
        return new Assembly(statements);
    }

    @Override
    protected boolean childAssmGoesFirst() {
        return false;
    }

    /**
     * Function declarations are a special case, because we the code to
     * be generated within the function comes after the function's label,
     * not before.
     */
    /*
    @Override
    public Assembly generateAssembly() {
        Assembly[] childAssm = new Assembly[subtreeTiles.size()];
        for (int i = 0; i < childAssm.length; i++)
            childAssm[i] = subtreeTiles.get(i).generateAssembly();

        Assembly localAssm = generateLocalAssembly();
        for (int i = 0; i < childAssm.length; i++) {
            localAssm.statements.addAll(localAssm.statements.size()-1, childAssm[i].statements);
        }
        return localAssm;
    }
    */
}