package yh326.ast.util;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDecl;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.VariableType;

public class LoadMethod {
    public static void loadMethod(SymbolTable sTable, String id, 
            FunctionTypeDeclList args, RetvalList rets) throws Exception {
        List<VariableType> argList = new ArrayList<VariableType>();
        List<VariableType> retList = new ArrayList<VariableType>();
        if (args != null) {
            for (Node varDecl : args.children) {
                if (varDecl instanceof FunctionTypeDecl) {
                    argList.add((VariableType) ((FunctionTypeDecl) varDecl).typeCheck(sTable));
                }
            }
        }
        if (rets != null) {
            for (Node varDecl : rets.children) {
                if (varDecl instanceof TypeNode) {
                    retList.add((VariableType) ((TypeNode) varDecl).typeCheck(sTable));
                }
                
            }
        }

        sTable.addFunc(id, argList, retList);
    }
}
