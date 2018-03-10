package yh326.ast.node.funcdecl;

import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
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
    
    public IRNode translate() {
    	IRTemp target = new IRTemp(id.getId());
    	IRTemp source = new IRTemp("ARG_" + "L" + line + "C" + col);
    	return new IRMove(target, source);
    }
}
