package yh326.ast.node.method;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDecl;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.stmt.StmtList;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableNodeType;
import yh326.ast.util.LoadMethod;

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

    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        // Interface and Method class share the same loadMethod method.
        // So it is moved to util package.
        LoadMethod.loadMethod(sTable, id.value, args, rets);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.enterBlock();
        for (Node varDecl : this.args.children) {
            if (varDecl instanceof FunctionTypeDecl) {
                FunctionTypeDecl funcVarDecl = (FunctionTypeDecl) varDecl;
                NodeType t = funcVarDecl.typeCheck(sTable);
                if (t instanceof VariableNodeType) {
                    sTable.addVar(funcVarDecl.getId().value, (VariableNodeType) t);
                } else {
                    sTable.exitBlock();
                    throw new RuntimeException("Unexpected Error.");
                }
            }
            
        }
        NodeType type = block.typeCheck(sTable);
        sTable.exitBlock();
        return type;
    }
}
