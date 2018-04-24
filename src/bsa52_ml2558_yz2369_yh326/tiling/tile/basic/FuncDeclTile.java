package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

import java.util.LinkedList;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;

public class FuncDeclTile extends Tile {
    @Override
    public boolean fits(IRNode root) {
        if (root instanceof IRFuncDecl) {
            this.root = root;

            IRFuncDecl decl = (IRFuncDecl) root;

            this.subtreeRoots = new LinkedList<>();
            subtreeRoots.add(decl.body());

            System.out.println("Tiling: FuncDecl Found! " + decl.name());
            return true;
        } else
            return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected Assembly generateLocalAssembly() {
        IRFuncDecl decl = (IRFuncDecl) root;

        LinkedList<AssemblyStatement> statements = new LinkedList<>();
        // assembly annotations for function
        statements.add(new AssemblyStatement(".text"));
        statements.add(new AssemblyStatement(".globl " + decl.name()));
        statements.add(new AssemblyStatement(".type " + decl.name() + ", @function"));
        statements.add(new AssemblyStatement(decl.name() + ":", true));
        statements.add(new AssemblyStatement("push", "rbp")); // push frame pointer to stack
        statements.add(new AssemblyStatement("mov", new AssemblyOperand("rbp"), new AssemblyOperand("rsp"))); // save
                                                                                                              // the
                                                                                                              // current
                                                                                                              // stack
                                                                                                              // pointer
                                                                                                              // as
                                                                                                              // frame
                                                                                                              // pointer
        statements.add(new AssemblyStatement("sub", new AssemblyOperand("rsp"), new AssemblyOperand("STACKSIZE")));

        //String ft = freshTemp();
        freshTemp();
        freshTemp();

        //statements.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp()), new AssemblyOperand(freshTemp())));
        //statements.add(new AssemblyStatement("mov", /*new AssemblyOperand(freshTemp())*/ new AssemblyOperand("QWORD PTR [rbp]"), new AssemblyOperand("rbx"))); // save
//                                                                                                                     callee
//                                                                                                                     safe
//                                                                                                                     register
//                                                                                                                     rbx
        //statements.add(new AssemblyStatement("mov", ft, ft));
        //statements.add(new AssemblyStatement("mov", /*new AssemblyOperand(freshTemp())*/new AssemblyOperand("QWORD PTR [rbp-8]"), new AssemblyOperand("rbp"))); // save
                                                                                                                    // callee
                                                                                                                    // safe
                                                                                                                    // register
                                                                                                                    // rbp

        return new Assembly(statements);
    }

    @Override
    protected boolean childAssmGoesFirst() {
        return false;
    }

    /**
     * Function declarations are a special case, because we the code to be generated
     * within the function comes after the function's label, not before.
     */
    /*
     * @Override public Assembly generateAssembly() { Assembly[] childAssm = new
     * Assembly[subtreeTiles.size()]; for (int i = 0; i < childAssm.length; i++)
     * childAssm[i] = subtreeTiles.get(i).generateAssembly();
     * 
     * Assembly localAssm = generateLocalAssembly(); for (int i = 0; i <
     * childAssm.length; i++) {
     * localAssm.statements.addAll(localAssm.statements.size()-1,
     * childAssm[i].statements); } return localAssm; }
     */
}
