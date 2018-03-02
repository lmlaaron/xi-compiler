package yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.MatchTypeException;
import yh326.exception.MismatchNumberException;
import yh326.exception.NotDefinedException;
import yh326.exception.OtherException;
import yh326.util.Tuple;

public class MethodCall extends Expr {
    private Identifier id;

    /**
     * Constructor
     * @param line
     * @param col
     * @param id
     */
    public MethodCall(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // This method combined method call and procedure call.
        
        // First check if id is defined as a variable.
        if (sTable.getVariableType(id.value) != null) {
            throw new OtherException(line, col, id.value + " is not a function");
        }
        
        Tuple<NodeType, NodeType> funcType = sTable.getFunctionType(id.value);
        if (funcType == null) {
            // Function not found
            throw new NotDefinedException(line, col, id.value);
        } else if (funcType.t1 instanceof UnitType) {
            // Function is a procedure
            if (children.size() == 1) {
                return funcType.t2;
            } else {
                throw new MismatchNumberException(line, col, 0, children.size() - 1);
            }
        } else if (funcType.t1 instanceof VariableType) {
            // Function returns one value
            if (children.size() == 2) {
                VariableType type = (VariableType) children.get(1).typeCheck(sTable);
                if (type.equals(funcType.t1)) {
                    return funcType.t2;
                } else {
                    throw new MatchTypeException(line, col, funcType.t1, type);
                }
            } else {
                throw new MismatchNumberException(line, col, 1, children.size() - 1);
            }
        } else {
            // Function returns multiple values
            List<VariableType> expected = ((ListVariableType) funcType.t1).getVariableTypes();
            List<VariableType> actual = new ArrayList<VariableType>();
            for (int i = 1; i < children.size(); i++) {
                actual.add((VariableType) children.get(i).typeCheck(sTable));
            }
            if (actual.size() != expected.size()) {
                throw new MismatchNumberException(line, col, expected.size(), children.size() - 1);
            } else {
                for (int i = 0; i < actual.size(); i++) {
                    if (!actual.get(i).equals(expected.get(i))) {
                        throw new MatchTypeException(line, col, expected.get(i), actual.get(i));
                    }
                }
            }
            return funcType.t2;
        }
        
    }
}
