package yh326.ast.node.stmt;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.AlreadyDefinedException;

public class VarDecl extends Stmt {
    private Identifier id;
    private TypeNode typeNode;
    
    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.id = id;
        this.typeNode = typeNode;
    }
    
    public Identifier getId() {
    	return id;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        addVarToTable(sTable, id.value);
        return new UnitType();
    }
    
    public NodeType typeCheckAndReturn(SymbolTable sTable) throws Exception {
        return addVarToTable(sTable, id.value);
    }
    
    public VariableType addVarToTable(SymbolTable sTable, String id) throws Exception {
     // Get NodeType from typeNode
        // For example, from Node ([] int) get NodeType int[]
        VariableType t = (VariableType) typeNode.typeCheck(sTable);
        // Add the combination to the context.
        // If it's already in the context, an exception is thrown.
        if (sTable.addVar(id, t) == false) {
            throw new AlreadyDefinedException(line, col, id);
        }
        return t;
    }
    
    @Override
    public IRNode translate() {
    	return new IRTemp(id + "L" + line + "C" + col);
    }
}
