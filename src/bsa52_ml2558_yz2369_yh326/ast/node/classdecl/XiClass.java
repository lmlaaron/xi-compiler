/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.*;

public class XiClass extends Node {

    // 'global' registry for classes
    public static List<XiClass> all = new LinkedList<XiClass>();

    public static int RUNTIME_RESOLVE = -1;
    public XiClass super_class; // super_class of the current class, might be NULL
    public Identifier classId;
    public String superClassName;

    public boolean hasInterface;
    public List<String> vars_ordered; // list of member variables
    public List<String> funcs_ordered; // list of member functions

    // below are two maps that store the name of func/var to its indexes.
    public HashMap<String, Integer> var_map = new HashMap<String, Integer>();
    public HashMap<String, Integer> func_map = new HashMap<String, Integer>();

    /**
     * Constructor TODO: need to reimplement this for parsing
     * 
     * @param line
     * @param col
     * @param id
     */
    public XiClass(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "class"), id);
        init(this, line, col, id, null);
    }

    public XiClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, new Keyword(line, col, "class"), id, extend);
        init(this, line, col, id, extend);

    }

    /**
     * Operations common to all constructors
     */
    private static void init(XiClass instance, int line, int col, Identifier id, Identifier extend) {
        instance.classId = id;
        instance.super_class = null;
        instance.superClassName = extend == null ? null : extend.value;
        instance.hasInterface = false;
        instance.vars_ordered = new ArrayList<>();
        instance.funcs_ordered = new ArrayList<>();
        all.add(instance);
    }

    @Override
    public void loadClasses(SymbolTable sTable) throws Exception {
        if (sTable.addClass(this) == false)
            throw new AlreadyDefinedException(line, col, classId.value);

        if (superClassName != null) {
            this.super_class = sTable.getClass(superClassName);
            this.func_map = new HashMap<String, Integer>(this.super_class.func_map);
            this.var_map = new HashMap<String, Integer>(this.super_class.var_map);
        }

    }

    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        List<String> funcs_ordered = new ArrayList<>();
        if (hasInterface) {
            for (Node child : children) {
                if (child instanceof Method) {
                    funcs_ordered.add(((Method) child).id.value);
                }
            }
            if (!funcs_ordered.containsAll(this.funcs_ordered)) {
                throw new OtherException(line, col, "Some functions declaired in the interface are not implemented.");
            }
        }

        sTable.enterBlock();
        sTable.setCurClass(classId.value);
        for (Node child : children) {
            if (child instanceof VarDecl) {
                ((VarDecl) child).typeCheckAndReturn(sTable);
                ((VarDecl) child).getId().forEach(id -> vars_ordered.add(id.value));
            } else if (child instanceof Method) {
                String funcName = ((Method) child).id.value;
                if (hasInterface) {
                    if (sTable.getFunctionType(this, funcName) == null) {
                        throw new OtherException(line, col,
                                "Function \"" + funcName + "\" is not declaired in the interface.");
                    }
                } else {
                    this.funcs_ordered.add(funcName);
                }
                child.loadMethods(sTable);
            }
        }
        sTable.setCurClass(null);
        sTable.exitBlock();
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.setCurClass(classId.value);
        sTable.enterBlock();
        // Instance variables have already been typechecked
        for (Node child : children) {
            if (child instanceof Method) {
                child.typeCheck(sTable);
            }
        }

        sTable.setCurClass(null);
        sTable.exitBlock();
        System.out.println("\nFinished typechecking class \"" + classId.value + "\"");
        System.out.println("Instance variables:");
        System.out.println(vars_ordered);
        System.out.println("Functions:");
        System.out.println(funcs_ordered);
        return new UnitType();
    }

    @Override
    public IRNode translate() {
        return null;
        // deprecated code, should not be called anyway
    }

    public int numVariables() {
        if (super_class == null) {
            return vars_ordered.size();
        } else {
            return vars_ordered.size() + super_class.numVariables();
        }
    }

    public int numMethods() {
        if (super_class == null) {
            return funcs_ordered.size();
        } else {
            return funcs_ordered.size() + super_class.numMethods();
        }
    }

    public IRFuncDecl getInitFunction() {
        // TODO: if we're only given the interface for a class, we shouldn't be
        // generating this function. That's only our responsibility if we
        // have the actual implementation!!!

        List<IRStmt> body = new LinkedList<IRStmt>();

        // step 1: recursively initialize superclasses
        if (super_class != null)
            body.add(new IRExp(new IRCall(new IRName("_I_init_" + superClassName), new LinkedList<IRExpr>())));

        // step 2: compute total size of this class
        int localSize = vars_ordered.size();
        String totalsizevar = Utilities.freshTemp();
        body.add(new IRMove(new IRTemp(totalsizevar), new IRConst(localSize + 1))); // +1 for the DV itself
        if (super_class != null) {
            // TODO: how to represent the size variable at IR level?
            IRExpr superSize = new IRTemp("_I_size_" + superClassName);
            body.add(new IRMove(new IRTemp(totalsizevar),
                    new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(totalsizevar), superSize)));
        }
        // TODO: ^^^ same as above
        IRExpr thisSize = new IRTemp("_I_size_" + classId.value);
        body.add(new IRMove(thisSize, new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(totalsizevar), new IRConst(8))));

        // step 3: allocate dispatch vector
        // TODO: right now I'm assuming we're not overriding anything,
        // so we need to allocate a spot for each method in each class
        int treeDVSize = sizeOfListOfIRMethods();// listOfIRMethods().size();
        XiClass xc = this;
        while (xc.super_class != null) {
            xc = xc.super_class;
            treeDVSize += xc.sizeOfListOfIRMethods(); // .size();
        }
        // TODO: don't know how to represent this variable at IR level
        IRExpr dv = new IRTemp("_I_vt_" + classId.value);
        //LinkedList<IRExpr> alloc_size = new LinkedList<>();
        //alloc_size.add(new IRConst(treeDVSize * 8));
        //body.add(new IRMove(dv, new IRCall(new IRName("_xi_alloc"), alloc_size)));
        int parentDVsize = 0;
        // if there is a parent, copy over its method pointers
        if (super_class != null) {
            // TODO: same representation issue:
            IRTemp superdv = new IRTemp("_I_vt_" + superClassName);

            IRTemp index = new IRTemp(Utilities.freshTemp());
            IRTemp addrFrom = new IRTemp(Utilities.freshTemp());
            IRTemp addrTo = new IRTemp(Utilities.freshTemp());

            IRLabel top = new IRLabel("top_" + NumberGetter.uniqueNumberStr());
            IRLabel end = new IRLabel("end_" + NumberGetter.uniqueNumberStr());

            body.add(new IRMove(index, new IRConst(0)));
            body.add(new IRMove(addrFrom, new IRMem(superdv)));
            body.add(new IRMove(addrTo, new IRMem(dv)));

            body.add(top);
            parentDVsize = treeDVSize - vars_ordered.size();

            // loop: copy over super's pointers
            body.add(new IRCJump(new IRBinOp(IRBinOp.OpType.GEQ, index, new IRConst(parentDVsize)), end.name()));
            body.add(new IRMove(new IRMem(addrTo), new IRMem(addrFrom)));
            body.add(new IRMove(addrFrom, new IRBinOp(IRBinOp.OpType.ADD, addrFrom, new IRConst(8))));
            body.add(new IRMove(addrTo, new IRBinOp(IRBinOp.OpType.ADD, addrTo, new IRConst(8))));
            body.add(new IRMove(index, new IRBinOp(IRBinOp.OpType.ADD, index, new IRConst(1))));
            body.add(new IRJump(new IRName(top.name())));
        }
        // copy over this class's method pointers:
        for (String func : this.func_map.keySet()) {
            body.add(new IRMove(
                    // TODO: if we're postprocessing func_map to get global indices, don't need to
                    // add parentDVsize
                    new IRMem(
                            new IRBinOp(IRBinOp.OpType.ADD, dv, new IRConst(8 * (parentDVsize + func_map.get(func))))),
                    new IRName(func)));
        }

        body.add(new IRReturn());

        IRFuncDecl f = new IRFuncDecl("_I_init_" + classId.value, // TODO: escape underscore characters in 'id'
                new IRSeq(body));

        return f;
    }

    public int sizeOfListOfIRMethods() {
        List<IRFuncDecl> list = new ArrayList<>();
        int ret = 0;
        for (Node child : children) {
            if (child instanceof Method) {
                ret = ret + 1;
            }
        }
        return ret;
    }

    public List<IRFuncDecl> GenerateListOfIRMethods() {
        List<IRFuncDecl> list = new ArrayList<>();

       list.add(getInitFunction());

        for (Node child : children) {
            if (child instanceof Method) {
                // though in symboltable for typechecking the THIS pointer is added to the
                // argument list
                // in ast the argument list still does not contain THIS pointer
                // thus we add THIS pointer to argument list just before translating into IR
                ((Method) child).addObjArgs(this);

                IRFuncDecl funcdecl = (IRFuncDecl) child.translate();
                // System.out.println("label " +funcdecl.label() + " name :" + funcdecl.name());
                IRFuncDecl f = new IRFuncDecl(((Method) child).id.value, funcdecl.body());
                list.add(funcdecl);
            }
        }
        return list;
    }

    // old implementation of IndexOfVar
    public int IndexOfVar(String varname) {
        // if there is no superclass or superclass is fully resolved
        // just return the index
        if (super_class != null) {
            if (super_class.IndexOfVar(varname) != RUNTIME_RESOLVE) {
                return super_class.IndexOfVar(varname);
            }
            return super_class.numVariables() + vars_ordered.indexOf(varname);
        } else {
            int i = 0;
            return vars_ordered.indexOf(varname);
        }
    }

    // new implementation of IndexOfVar
    public int IndexOfVar_new(String varname) {
        var_map.putIfAbsent(varname, var_map.size() + 1);
        return var_map.get(varname);
    }

    // old implementation of IndexOfFunc
    public int IndexOfFunc(String funcname) {
        if (super_class != null) {
            if (super_class.IndexOfFunc(funcname) != RUNTIME_RESOLVE) {
                return super_class.IndexOfFunc(funcname);
            }
            return super_class.numMethods() + funcs_ordered.indexOf(funcname);
        } else {
            return funcs_ordered.indexOf(funcname);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof XiClass) {
            XiClass otherClass = (XiClass) other;
            if (!classId.value.equals(otherClass.classId.value))
                return false;
            if (superClassName == null && otherClass.superClassName == null)
                return true;
            else if (superClassName != null && otherClass.superClassName != null)
                return superClassName.equals(otherClass.superClassName);
            else
                return false;
        } else
            return false;
    }

    // new implementation of IndexOfFunc
    public int IndexOfFunc_new(String funcname) {
        func_map.putIfAbsent(funcname, func_map.size() + 1);
        return func_map.get(funcname);
    }
}