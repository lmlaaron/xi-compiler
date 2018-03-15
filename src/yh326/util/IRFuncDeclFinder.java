package yh326.util;

import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

import java.util.ArrayList;

/*
Exists for the purpose of traversing an IR tree and finding all the function declarations
TODO: this may be broken in the future by the existence of class methods
 */
public class IRFuncDeclFinder extends IRVisitor {
    private ArrayList<IRFuncDecl> funcDecls;

    public IRFuncDeclFinder() {
        super(new IRNodeFactory_c());
        funcDecls = new ArrayList<>();
    }

    @Override
    protected IRVisitor enter(IRNode parent, IRNode n) {
        if (n instanceof IRFuncDecl)
            funcDecls.add((IRFuncDecl) n);
        return this;
    }

    public ArrayList<IRFuncDecl> getFuncDecls() {
        return funcDecls;
    }
}
