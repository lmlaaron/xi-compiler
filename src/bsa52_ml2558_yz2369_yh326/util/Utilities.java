package bsa52_ml2558_yz2369_yh326.util;

import java.util.*;
import java.util.stream.Collectors;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.method.Method;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class Utilities {
    
    /**
     * Load the given method into the symbol table. This is different from the
     * loadMethods method in Node class, which only does the first step - by reading
     * the content in the .ixi file to get the id, args, and rets of the function.
     * 
     * @param sTable
     * @param id
     * @param args
     * @param rets
     * @throws Exception
     */
    public static boolean loadMethod(SymbolTable sTable, String id, FunctionTypeDeclList args, RetvalList rets)
            throws Exception {
        sTable.enterBlock();
        List<VariableType> argList = new ArrayList<>();
        List<VariableType> retList = new ArrayList<>();
        if (args != null)
            for (Node varDecl : args.children)
                argList.add((VariableType) ((VarDecl) varDecl).typeCheckAndReturn(sTable));
        if (rets != null)
            for (Node varDecl : rets.children)
                retList.add((VariableType) ((TypeNode) varDecl).typeCheck(sTable));
        sTable.exitBlock();
        return sTable.addFunc(id, argList, retList);
    }

    public static String toIRFunctionName(String name, List<VariableType> args, List<VariableType> rets) {
        String result = "_I" + name.replace("_", "__") + "_";
        if (rets.size() == 0) {
            result += "p";
        } else if (rets.size() == 1) {
            result += rets.get(0).toShortString();
        } else {
            result += "t" + rets.size();
            for (VariableType type : rets) {
                result += type.toShortString();
            }
        }
        for (VariableType type : args) {
            result += type.toShortString();
        }
        return result;
    }
    
    public static String toIRGlobalName(String name, VariableType type) {
        String result = "_I_g_" + name.replace("_", "__") + "_";
        result+= type.toShortString();
        return result;
    }
    
    public static IRESeq xiAlloc(IRExpr size) {
        String labelNumber = NumberGetter.uniqueNumberStr();
        List<IRStmt> stmts = new ArrayList<IRStmt>();

        IRExpr realSize;
        if (size instanceof IRConst) {
            realSize = new IRConst(((IRConst) size).constant() * 8 + 8);
        } else {
            IRBinOp mult = new IRBinOp(OpType.MUL, size, new IRConst(8));
            realSize = new IRBinOp(OpType.ADD, mult, new IRConst(8));
        }
        IRTemp arrayLen = new IRTemp("_arrayLen_" + labelNumber);
        stmts.add(new IRMove(arrayLen, new IRCall(new IRName("_xi_alloc"), realSize)));
        stmts.add(new IRMove(new IRMem(arrayLen), size));
        IRTemp newArray = new IRTemp("_array_" + labelNumber);
        stmts.add(new IRMove(newArray, new IRBinOp(OpType.ADD, arrayLen, new IRConst(8))));

        return new IRESeq(new IRSeq(stmts), newArray);
    }

    private static HashSet<String> realRegisters;

    private static void initRegisters() {
        if (realRegisters == null) {
            realRegisters = new HashSet<>();

            realRegisters.add("rax");
            realRegisters.add("rbx");
            realRegisters.add("rcx");
            realRegisters.add("rdx");

            realRegisters.add("rsi");
            realRegisters.add("rdi");

            realRegisters.add("rbp");
            realRegisters.add("rsp");

            for (int i = 8; i <= 15; i++) {
                realRegisters.add("r" + Integer.toString(i));
            }


            // divide between caller, callee save registers
            calleeSaveRegisters = new HashSet<>();

            calleeSaveRegisters.add("rbx");
            calleeSaveRegisters.add("rbp");
            calleeSaveRegisters.add("r12");
            calleeSaveRegisters.add("r13");
            calleeSaveRegisters.add("r14");
            calleeSaveRegisters.add("r15");

            callerSaveRegisters = new HashSet<>(
                realRegisters.stream().filter(
                    r -> isRegisterForAllocation(r) && !calleeSaveRegisters.contains(r)
                ).collect(Collectors.toSet())
            );
        }
    }

    private static HashSet<String> callerSaveRegisters;
    private static HashSet<String> calleeSaveRegisters;

    public static boolean isCallerSave(String reg) {
        return callerSaveRegisters.contains(reg);
    }
    public static boolean isCalleeSave(String reg) {
        return calleeSaveRegisters.contains(reg);
    }

    public static Set<String> callerSaveRegisters() {
        return new HashSet<>(callerSaveRegisters);
    }


    public static boolean isRealRegister(String s) {
        initRegisters();
        return realRegisters.contains(s);
    }

    public static List<String> registersForAllocation() {
        initRegisters();
        LinkedList<String> ret = new LinkedList<String>(realRegisters);
        ret.remove("rsp");
        ret.remove("rbp");
        return ret;
    }

    public static boolean isRegisterForAllocation(String r) {
        initRegisters();
        return realRegisters.contains(r) && !r.equals("rsp") && !r.equals("rbp");
    }

    public static Set<String> allRegisters() {
        initRegisters();
        return new HashSet<>(realRegisters);
    }

    public static String freshTemp() {
        return "__FreshTemp_" + NumberGetter.uniqueNumberStr();
    }

    public static boolean isNumber(String s) {
        return s.matches("-?\\d+");
    }
}
