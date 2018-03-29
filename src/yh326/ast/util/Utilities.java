package yh326.ast.util;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.stmt.VarDecl;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.VariableType;

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
}
