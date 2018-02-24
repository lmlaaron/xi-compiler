package yh326.ast.util;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDecl;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.type.FunctionNodeType;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableNodeType;

public class LoadMethod {
    public static void loadMethod(SymbolTable sTable, String id, 
            FunctionTypeDeclList args, RetvalList rets) throws Exception {
        List<VariableNodeType> argList = new ArrayList<VariableNodeType>();
        List<VariableNodeType> retList = new ArrayList<VariableNodeType>();
        if (args != null) {
            for (Node varDecl : args.children) {
                if (varDecl instanceof FunctionTypeDecl) {
                    NodeType t = ((FunctionTypeDecl) varDecl).typeCheck(sTable);
                    if (t instanceof VariableNodeType) {
                        argList.add((VariableNodeType) t);
                    } else {
                        throw new RuntimeException("Unexpected Error.");
                    }
                }
                
            }
        }
        if (rets != null) {
            for (Node varDecl : rets.children) {
                if (varDecl instanceof FunctionTypeDecl) {
                    NodeType t = ((FunctionTypeDecl) varDecl).typeCheck(sTable);
                    if (t instanceof VariableNodeType) {
                        retList.add((VariableNodeType) t);
                    } else {
                        throw new RuntimeException("Unexpected Error.");
                    }
                }
                
            }
        }
            
        FunctionNodeType funcType = new FunctionNodeType(argList, retList);
        sTable.addFunc(id, funcType);
    }
}
