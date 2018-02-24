package yh326.ast.node.method;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.stmt.StmtList;
import yh326.ast.type.FunctionNodeType;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class Method extends Node {
    private Identifier id;
    private FunctionTypeDeclList args;
    private RetvalList rets;
    private StmtList block;

    public Method(int line, int col, Identifier id, FunctionTypeDeclList args, RetvalList rets, StmtList b) {
        super(line, col, id, args, rets, b);
        this.id = id;
        this.args = args;
        this.rets = rets;
        this.block = b;
    }

    public void loadMethods(SymbolTable sTable) throws Exception {
        List<VariableNodeType> args = new ArrayList<VariableNodeType>();
        List<VariableNodeType> rets = new ArrayList<VariableNodeType>();
        for (Node varDecl : this.args.children) {
            NodeType t = varDecl.typeCheck(sTable);
            if (t instanceof VariableNodeType) {
                args.add((VariableNodeType) t);
            } else {
                throw new TypeErrorException("Unexpected Error.");
            }
        }
        for (Node varDecl : this.rets.children) {
            NodeType t = varDecl.typeCheck(sTable);
            if (t instanceof VariableNodeType) {
                rets.add((VariableNodeType) t);
            } else {
                throw new TypeErrorException("Unexpected Error.");
            }
        }
            
        FunctionNodeType funcType = new FunctionNodeType(args, rets);
        sTable.addFunc(id.value, funcType);
    }
}
