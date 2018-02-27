package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;

public class VarDecl extends Stmt {
    private Identifier id;
    private TypeNode typeNode;
    
    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.id = id;
        this.typeNode = typeNode;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // Get NodeType from typeNode
        // For example, from Node ([] int) get NodeType int[]
        VariableType t = (VariableType) typeNode.typeCheck(sTable);
        // Add the combination to the context.
        // If it's already in the context, an exception is thrown.
        sTable.addVar(id.value, t);
        return new UnitType();
    }
}
