package yh326.ast.node.method;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.funcdecl.FunctionTypeDecl;
import yh326.ast.node.funcdecl.FunctionTypeDeclList;
import yh326.ast.node.retval.RetvalList;
import yh326.ast.node.stmt.StmtList;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.ast.util.LoadMethod;
import yh326.exception.AlreadyDefinedException;
import yh326.exception.OtherException;

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
        if (LoadMethod.loadMethod(sTable, id.value, args, rets) == false) {
            throw new AlreadyDefinedException(line, col, id.value);
        }
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // Loading arguments into the symbol table
        if (args != null) {
            for (Node varDecl : args.children) {
                if (varDecl instanceof FunctionTypeDecl) {
                    FunctionTypeDecl funcVarDecl = (FunctionTypeDecl) varDecl;
                    VariableType t = (VariableType) funcVarDecl.typeCheck(sTable);
                    // Check if function argument variable name has been defined
                    // as some function name.
                    String varId = funcVarDecl.getId().value;
                    if (sTable.getFunctionType(varId) != null ||
                            sTable.addVar(varId, t) == false) {
                        throw new AlreadyDefinedException(line, col, id.value);
                    }
                }
            }
        }

        sTable.setCurFunction(id.value);
        NodeType actual = new UnitType();
        NodeType expected = sTable.getFunctionType(id.value).t2;
        if (block != null) {
            actual = block.typeCheck(sTable);
        }
        sTable.setCurFunction(null);
        if (actual instanceof UnitType && !(expected instanceof UnitType)) {
            throw new OtherException(line, col, "Missing return statement");
        }
        return new UnitType();
    }
}
