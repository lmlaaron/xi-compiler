package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class VarDecl extends Stmt {
    private Identifier id;
    private TypeNode typeNode;
    
    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.id = id;
        this.typeNode = typeNode;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType t = typeNode.typeCheck(sTable);
        if (t instanceof VariableNodeType) {
            sTable.addVar(id.value, (VariableNodeType) t);
            sTable.dumpTable();
            return new UnitNodeType();
        } else {
            throw new TypeErrorException("Unexpected Error");
        }
    }
}
