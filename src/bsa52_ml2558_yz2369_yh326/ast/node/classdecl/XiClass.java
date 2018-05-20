/**
 * Author: Mulong Luo
 * Date: April 30
 * Usage: ast node for Class definition
 */

package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import java.util.*;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.ThreeTuple;
import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.util.Copy;
import edu.cornell.cs.cs4120.xic.ir.*;

public class XiClass extends Node {

    // 'global' registry for classes
    public static List<XiClass> all = new LinkedList<XiClass>();

    public static int RUNTIME_RESOLVE = -1;
    public XiClass superClass = null;
    public Identifier classId;
    public String superClassName = null;
    public boolean hasInterface = false;
    public List<String> vars_ordered = new ArrayList<>();

    public List<String> funcs_ordered = new ArrayList<>();
    private List<String> overrides = new ArrayList<>();

    /**
     * Constructor TODO: need to reimplement this for parsing
     * 
     * @param line
     * @param col
     * @param id
     */
    public XiClass(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "class"), id);
        this.classId = id;
        all.add(this);
    }

    public XiClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, new Keyword(line, col, "class"), id, extend);
        this.classId = id;
        if (extend != null) {
            this.superClassName = extend.value;
        }
        all.add(this);
    }

    public List<ThreeTuple<String, Integer, XiClass>> getOverrides() {
        System.out.println("Class " + classId.value + " has " + overrides.size() + " overrides:");

        List<ThreeTuple<String, Integer, XiClass>> ret = new LinkedList<>();

        // get the total size of the dispatch vector to calculate
        // global offset
        int height = 0;
        XiClass xc = this;
        while (xc != null) {
            height += xc.funcs_ordered.size();
            xc = xc.superClass;
        }

        //System.out.println("\theight " + height);

        for (String override : overrides) {
            // iterate upwards until we find an occurrence
            int i = funcs_ordered.size()-1;
            xc = superClass;
            while (i < height) {
                // iterate through super class's methods
                Iterator<String> it = new LinkedList(xc.funcs_ordered).descendingIterator();
                while (it.hasNext()) {
                    i++;
                    if (it.next().equals(override)) {

                        //System.out.println("\ti " + i);

                        int global_i = height - i - 1;

                        //System.out.println("\t" + override + " has global index " + global_i + " and appears in parent " + xc.classId);

                        ret.add(new ThreeTuple<>(override, global_i, xc));
                        //System.out.println(override+ " " +String.valueOf(global_i));
                        i = height; // break the external loop
                        break;
                    }
                }
                xc = xc.superClass;
            }
        }
        return ret;
    }

    @Override
    public void loadClasses(SymbolTable sTable) throws Exception {
        if (sTable.addClass(this) == false)
            throw new AlreadyDefinedException(line, col, classId.value);

        if (superClassName != null) {
            this.superClass = sTable.getClass(superClassName);
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
                ((VarDecl) child).ids.forEach(id -> vars_ordered.add(id.value));
            } else if (child instanceof Method) {

                String funcName = ((Method) child).id.value;
        	    	//System.out.println(funcName);
                boolean isOverride = sTable.isOverride(this, funcName);
                if (isOverride) {
                	   //System.out.println("isOverride");
                } else {
                	  //System.out.println("isNotOverride");
                }
                if (hasInterface) {
                    if (sTable.getFunctionType(this, funcName) == null) {
                        throw new OtherException(line, col,
                                "Function \"" + funcName + "\" is not declaired in the interface.");
                    }
                    if (isOverride) {
                        this.funcs_ordered.remove(funcName);
                        overrides.add(funcName);
                    }
                } else {
                    if (!isOverride) {
                        this.funcs_ordered.add(funcName);
                    } else {
                        overrides.add(funcName);
                    }
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
        return new UnitType();
    }

    @Override
    public IRNode translate() {
        return null;
        // deprecated code, should not be called anyway
    }

    public int numVariables() {
        if (superClass == null) {
            return vars_ordered.size();
        } else {
            return vars_ordered.size() + superClass.numVariables();
        }
    }

    public int numMethods() {
        if (superClass == null) {
            return funcs_ordered.size();
        } else {
            return funcs_ordered.size() + superClass.numMethods();
        }
    }

    public IRFuncDecl getInitFunction(SymbolTable sTable) {
        // TODO: if we're only given the interface for a class, we shouldn't be
        // generating this function. That's only our responsibility if we
        // have the actual implementation!!!

        IRLabel veryEnd = new IRLabel("_veryEnd_" + NumberGetter.uniqueNumberStr());

        List<IRStmt> body = new LinkedList<IRStmt>();

        // step 1: recursively initialize superclasses
        if (superClass != null)
           body.add(new IRExp(new IRCall(new IRName("_I_init_" + superClassName.replace("_", "__")), new LinkedList<IRExpr>())));

        // if initialized already, just return
        // TODO: WHY DOES THIS SEGFAULT?
        IRExpr thisSize = new IRName("_I_size_" + classId.value.replace("_", "__"));
        //IRTemp thisSizeTemp = new IRTemp(Utilities.freshTemp());
        //body.add(new IRMove(thisSizeTemp, thisSize));
        //body.add(new IRCJump(new IRBinOp(IRBinOp.OpType.NEQ, thisSizeTemp, new IRConst(0)), veryEnd.name()));

        // step 2: compute total size of this class
        int localSize = vars_ordered.size();
        String totalsizevar = Utilities.freshTemp();
        body.add(new IRMove(new IRTemp(totalsizevar), new IRConst(localSize + 1))); // +1 for the DV itself

        // TODO: ^^^ same as above

        body.add(new IRMove(thisSize, new IRBinOp(IRBinOp.OpType.MUL, new IRTemp(totalsizevar), new IRConst(8))));

        if (superClass != null) {
            // TODO: how to represent the size variable at IR level?
            IRExpr superSize = new IRName("_I_size_" + superClassName.replace("_", "__"));
            body.add(new IRMove(new IRTemp(totalsizevar),
                    new IRBinOp(IRBinOp.OpType.ADD, new IRTemp(totalsizevar), superSize)));
        }

        // step 3: allocate dispatch vector
        // TODO: right now I'm assuming we're not overriding anything,
        // so we need to allocate a spot for each method in each class
        int treeDVSize = sizeOfListOfIRMethods();// listOfIRMethods().size();
        XiClass xc = this;
        while (xc != null) {
            treeDVSize += xc.funcs_ordered.size();
            xc = xc.superClass;
        }
        // TODO: don't know how to represent this variable at IR level
        IRExpr dv = new IRName("_I_vt_" + classId.value.replace("_", "__"));
        LinkedList<IRExpr> alloc_size = new LinkedList<>();
        alloc_size.add(new IRConst(treeDVSize * 8));
        //IRTemp temp = new IRTemp(Utilities.freshTemp());
        body.add(new IRMove(dv, new IRCall(new IRName("_xi_alloc"), alloc_size)));  //cannot do xi_alloc in ctors section, need static allocation
        //body.add(new IRMove(dv, dv));  // use to generate lea rax, dv; mov dv, rax see IRMove Tile conditions: src and dest are same, also begin with _I_vt_  , see MoveTile.java
        //body.add(new IRMove(dv, temp));
        //body.add(new IRMove(dv ,new IRBinOp(IRBinOp.OpType.ADD,dv, new IRConst(8))));

        int parentDVsize = 0;
        // if there is a parent, copy over its method pointers
        if (superClass != null) {
            // TODO: same representation issue:
            IRName superdv = new IRName("_I_vt_" + superClassName.replace("_", "__"));

            IRTemp index = new IRTemp(Utilities.freshTemp());
            IRTemp addrFrom = new IRTemp(Utilities.freshTemp());
            IRTemp addrTo = new IRTemp(Utilities.freshTemp());

            IRLabel top = new IRLabel("top_" + NumberGetter.uniqueNumberStr());
            IRLabel then = new IRLabel("then_" + NumberGetter.uniqueNumberStr());
            IRLabel end = new IRLabel("end_" + NumberGetter.uniqueNumberStr());

            body.add(new IRMove(index, new IRConst(0)));
            body.add(new IRMove(addrFrom, superdv));  // dispatch vector is already the address of the function pointers!!! DON'T use IRMem(superdv)!!!
            body.add(new IRMove(addrTo, dv));

            body.add(top);
            //parentDVsize = treeDVSize - vars_ordered.size(); // not sure if this is a bug, ask bsa52
            parentDVsize = superClass.numMethods();

            // loop: copy over super's pointers
            body.add(new IRCJump(new IRBinOp(IRBinOp.OpType.GEQ, index, new IRConst(parentDVsize)), end.name(), then.name()));
            body.add(then);
            body.add(new IRMove(new IRMem(addrTo), new IRMem(addrFrom)));
            body.add(new IRMove(addrFrom, new IRBinOp(IRBinOp.OpType.ADD, addrFrom, new IRConst(8))));
            body.add(new IRMove(addrTo, new IRBinOp(IRBinOp.OpType.ADD, addrTo, new IRConst(8))));
            body.add(new IRMove(index, new IRBinOp(IRBinOp.OpType.ADD, index, new IRConst(1))));
            body.add(new IRJump(new IRName(top.name())));
            body.add(end);
        }

        // copy over this class's method pointers:
        for (int i = 0; i < funcs_ordered.size(); i++) {
            // figure out the ABI name
            List<VariableType> argTypes = new ArrayList<>();
            List<VariableType> retTypes = new ArrayList<>();
            Tuple<NodeType, NodeType> funcType = sTable.getFunctionType(this, funcs_ordered.get(i) );
            if (funcType.t1 instanceof VariableType) {
                // Function returns one value
                argTypes.add((VariableType) funcType.t1);
            } else {
                // Function returns multiple values
                argTypes = ((ListVariableType) funcType.t1).getVariableTypes();
            }

            // Store return type
            if (funcType.t2 instanceof UnitType) {
                // Function is a procedure, do nothing
            } else if (funcType.t2 instanceof VariableType) {
                // Function returns one value
                retTypes.add((VariableType) funcType.t2);
            } else {
                retTypes = ((ListVariableType) funcType.t2).getVariableTypes();
            }
            body.add(new IRMove(
                    // TODO: if we're postprocessing func_map to get global indices, don't need to
                    // add parentDVsize
                    new IRMem(
                            new IRBinOp(IRBinOp.OpType.ADD, dv, new IRConst(8 * (parentDVsize + i)))),
                    //new IRName(funcs_ordered.get(i))));
                    new IRName(Utilities.toIRFunctionName(funcs_ordered.get(i),argTypes,retTypes))));
        }


        // perform overrides:
        // note: this block and the one above are nearly identical, but
        // we don't have to maintain this code HOORAY
        List<ThreeTuple<String, Integer, XiClass>> overrideList = getOverrides();
        //System.out.println(classId.getId()+ " size of overrideList "+ String.valueOf(overrideList.size()));
        //System.out.println(classId.getId() + "size of funcs_ordered "+ String.valueOf(funcs_ordered.size()));
        for (int i = 0; i < overrideList.size(); i++) {
            ThreeTuple<String, Integer, XiClass> tup = overrideList.get(i);

            // figure out the ABI name
            List<VariableType> argTypes = new ArrayList<>();
            List<VariableType> retTypes = new ArrayList<>();
            Tuple<NodeType, NodeType> funcType = sTable.getFunctionType(this, tup.t1 );
            if (funcType.t1 instanceof VariableType) {
                // Function returns one value
                argTypes.add((VariableType) funcType.t1);
            } else {
                // Function returns multiple values
                argTypes = ((ListVariableType) funcType.t1).getVariableTypes();
            }

            // Store return type
            if (funcType.t2 instanceof UnitType) {
                // Function is a procedure, do nothing
            } else if (funcType.t2 instanceof VariableType) {
                // Function returns one value
                retTypes.add((VariableType) funcType.t2);
            } else {
                retTypes = ((ListVariableType) funcType.t2).getVariableTypes();
            }
            body.add(new IRMove(
                    // TODO: if we're postprocessing func_map to get global indices, don't need to
                    // add parentDVsize
                    new IRMem(
                            new IRBinOp(IRBinOp.OpType.ADD, dv, new IRConst(8 *  tup.t2))),
                    //new IRName(funcs_ordered.get(i))));
                    new IRName(Utilities.toIRFunctionName(tup.t1, argTypes, retTypes))));
        }


        //body.add(veryEnd);
        body.add(new IRReturn());
        IRFuncDecl f = new IRFuncDecl("_I_init_" + classId.value.replace("_", "__"),
                new IRSeq(body));

        return f;
    }

    // TODO: also copy over methods that this class overrides! currently the API doesn't exist yet

    public int sizeOfListOfIRMethods() {
        int ret = 0;
        for (Node child : children) {
            if (child instanceof Method) {
                ret = ret + 1;
            }
        }
        return ret;
    }

    public List<IRFuncDecl> GenerateListOfIRMethods(SymbolTable sTable) {
        List<IRFuncDecl> list = new ArrayList<>();

       list.add(getInitFunction(sTable));

        for (Node child : children) {
            if (child instanceof Method) {
                // though in symboltable for typechecking the THIS pointer is added to the
                // argument list
                // in ast the argument list still does not contain THIS pointer
                // thus we add THIS pointer to argument list just before translating into IR
                ((Method) child).addObjArgs(this);

                IRFuncDecl funcdecl = (IRFuncDecl) child.translate();
                list.add(funcdecl);
            }
        }
        return list;
    }

    public int indexOfVar(String varname) {
        return vars_ordered.indexOf(varname);
    }

    public int indexOfFunc(String funcname) {
        if (superClass != null) {
            if (superClass.indexOfFunc(funcname) != RUNTIME_RESOLVE) {
                return superClass.indexOfFunc(funcname);
            }
            return superClass.numMethods() + funcs_ordered.indexOf(funcname);
        } else {
            return funcs_ordered.indexOf(funcname);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof XiClass) {
            XiClass otherClass = (XiClass) other;
            return otherClass.classId.value.equals(this.classId.value);
        }
        else {
            return false;
        }
//        if (other instanceof XiClass) {
//            XiClass otherClass = (XiClass) other;
//            if (!classId.value.equals(otherClass.classId.value))
//                return false;
//            if (superClassName == null && otherClass.superClassName == null)
//                return true;
//            else if (superClassName != null && otherClass.superClassName != null)
//                return superClassName.equals(otherClass.superClassName);
//            else
//                return false;
//        } else
//            return false;
    }

    @Override
    public int hashCode() {
        return classId.value.hashCode();
    }
}