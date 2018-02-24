package yh326.ast.node.interfc;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.util.LoadMethod;

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
        // Interface and Method class share the same loadMethod method.
        // So it is moved to util package.
        LoadMethod.loadMethod(sTable, id.value, args, rets);
    }
}
