package yh326.ast.node.interfc;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.util.Utilities;
import yh326.exception.AlreadyDefinedException;

public class Interface extends Node {
    private Identifier id;
    private FunctionTypeDeclList args;
    private RetvalList rets;
    
    /**
     * Constructor
     * @param line
     * @param col
     * @param id
     * @param args
     * @param rets
     */
    public Interface(int line, int col, Identifier id, FunctionTypeDeclList args, RetvalList rets) {
        super(line, col, id, args, rets);
        this.id = id;
        this.args = args;
        this.rets = rets;
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        // Interface and Method class share the same loadMethod method.
        // So it is moved to util package.
        if (Utilities.loadMethod(sTable, id.value, args, rets) == false) {
            throw new AlreadyDefinedException(line, col, id.value);
        }
    }
}
