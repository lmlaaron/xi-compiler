package yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;
import yh326.util.Tuple;

public class MethodCall extends Expr {
    private Identifier id;

    public MethodCall(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // This method combined method call and procedure call.
        Tuple<NodeType, NodeType> funcType = sTable.getFunctionType(id.value);
        if (funcType.t1 instanceof UnitType) {
            if (children.size() == 1) {
                return funcType.t2;
            } else {
                throw new TypeErrorException("Expecting 0 argument, but got " + (children.size() - 1) + ".");
            }
        } else if (funcType.t1 instanceof VariableType) {
            if (children.size() == 2) {
                VariableType type = (VariableType) children.get(1).typeCheck(sTable);
                if (type.equals(funcType.t1)) {
                    return funcType.t2;
                } else {
                    throw new TypeErrorException(funcType.t1, type);
                }
            } else {
                throw new TypeErrorException("Expecting 1 argument, but got " + (children.size() - 1) + ".");
            }
        } else {
            List<VariableType> expected = ((ListVariableType) funcType.t1).getVariableTypes();
            List<VariableType> actual = new ArrayList<VariableType>();
            for (int i = 1; i < children.size(); i++) {
                actual.add((VariableType) children.get(i).typeCheck(sTable));
            }
            if (actual.size() != expected.size()) {
                throw new TypeErrorException("Expecting " + expected.size() + " arguments, but got " + actual.size() + ".");
            } else {
                for (int i = 0; i < actual.size(); i++) {
                    if (!actual.get(i).equals(expected.get(i))) {
                        throw new TypeErrorException(expected.get(i), actual.get(i));
                    }
                }
            }
            return funcType.t2;
        }
        
    }
}
