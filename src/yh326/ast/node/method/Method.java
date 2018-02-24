package yh326.ast.node.method;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.stmt.Block;
import yh326.ast.node.vardecl.VarDeclList;
import yh326.ast.type.FunctionNodeType;
import yh326.ast.type.NodeType;

public class Method extends Node {
    private Identifier id;
    private VarDeclList args;
    private RetvalList rets;
    private Block block;

    public Method(int line, int col, Identifier id, VarDeclList args, RetvalList rets, Block b) {
        super(line, col, id, args, rets, b);
        this.id = id;
        this.args = args;
        this.rets = rets;
        this.block = b;
    }

    public void loadMethods(SymbolTable sTable) throws Exception {
        List<NodeType> args = new ArrayList<NodeType>();
        List<NodeType> rets = new ArrayList<NodeType>();
        if (this.args != null) {
            for (Node varDecl : this.args.children) {
                args.add(varDecl.typeCheck(sTable));
            }
        }
        if (this.rets != null) {
            for (Node varDecl : this.rets.children) {
                rets.add(varDecl.typeCheck(sTable));
            }
        }
            
        FunctionNodeType funcType = new FunctionNodeType(args, rets);
        sTable.addFunc(id.value, funcType);
    }
}
