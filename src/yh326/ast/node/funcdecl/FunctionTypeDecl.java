package yh326.ast.node.funcdecl;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableType;
import yh326.exception.AlreadyDefinedException;

/**
 * Argument type or return type of a function.
 * @author Syugen
 *
 */
public class FunctionTypeDecl extends Node {
    private Identifier id;
    private TypeNode typeNode;
    
    public FunctionTypeDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.id = id;
        this.typeNode = typeNode;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = (VariableType) typeNode.typeCheck(sTable);
        if (sTable.getFunctionType(id.value) != null ||
                sTable.getVariableType(id.value) != null) {
            throw new AlreadyDefinedException(line, col, id.value);
        } else {
            return t;
        }
           
    }
    
    public Identifier getId() {
        return id;
    }
}
