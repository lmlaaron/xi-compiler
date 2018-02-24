package yh326.ast.node.interfc;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.type.FunctionNodeType;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableNodeType;

public class Interface extends Node {
    private Identifier id;
    private FunctionTypeDeclList args;
    private RetvalList rets;
    
    public Interface(int line, int col, Identifier id, FunctionTypeDeclList args, RetvalList rets) {
        super(line, col, id, args, rets);
        this.id = id;
        this.args = args;
        this.rets = rets;
    }
    
    public void loadMethods(SymbolTable sTable) throws Exception {
        List<VariableNodeType> args = new ArrayList<VariableNodeType>();
        List<VariableNodeType> rets = new ArrayList<VariableNodeType>();
        if (this.args != null) {
            for (Node varDecl : this.args.children) {
                NodeType t = varDecl.typeCheck(sTable);
                if (t instanceof VariableNodeType) {
                    args.add((VariableNodeType) t);
                }
            }
        }
        if (this.rets != null) {
            for (Node varDecl : this.rets.children) {
                NodeType t = varDecl.typeCheck(sTable);
                if (t instanceof VariableNodeType) {
                    rets.add((VariableNodeType) t);
                }
            }
        }
            
        FunctionNodeType funcType = new FunctionNodeType(args, rets);
        sTable.addFunc(id.value, funcType);
    }
}
