package bsa52_ml2558_yz2369_yh326.ast.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;

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
        List<VariableType> argList = new ArrayList<VariableType>();
        List<VariableType> retList = new ArrayList<VariableType>();
        if (args != null) {
            for (Node varDecl : args.children) {
                argList.add((VariableType) ((VarDecl) varDecl).typeCheckAndReturn(sTable));
            }
        }
        if (rets != null) {
            for (Node varDecl : rets.children) {
                retList.add((VariableType) ((TypeNode) varDecl).typeCheck(sTable));

            }
        }
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

    private static HashSet<String> realRegisters;
    private static void initRealRegisters() {
        if (realRegisters == null) {
            realRegisters = new HashSet<>();

            realRegisters.add("rax");
            realRegisters.add("rbx");
            realRegisters.add("rcx");
            realRegisters.add("rdx");

            realRegisters.add("rsx");
            realRegisters.add("rbx");

            realRegisters.add("rsi");
            realRegisters.add("rdi");

            realRegisters.add("rbp");
            realRegisters.add("rsp");

            for (int i = 8; i <= 15; i++) {
                realRegisters.add("r" + Integer.toString(i));
            }
        }
    }

    public static boolean isRealRegister(String s) {
        initRealRegisters();
        return realRegisters.contains(s);
    }

    public static boolean isNumber(String s) {
        return s.matches("-?\\d+");
    }
}
