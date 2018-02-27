package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.ast.type.VoidType;
import yh326.exception.TypeErrorException;

public class Return extends Stmt {

    public Return(int line, int col) {
        super(line, col, new Keyword(line, col, "return"));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType expected = sTable.getCurFuncReturnType();
        NodeType actual;
        
        // Nothing to be returned
        if (children.size() == 1) {
            actual = new UnitType();
        } else if (children.size() == 2) {
            actual = (VariableType) children.get(1).typeCheck(sTable);
        } else {
            List<VariableType> listType = new ArrayList<VariableType>();
            for (int i = 1; i < children.size(); i++) {
                listType.add((VariableType) children.get(i).typeCheck(sTable));
            }
            actual = new ListVariableType(listType);
        }
        
        if (actual.getClass() != expected.getClass()) {
            throw new TypeErrorException(expected, actual);
        } else {
            // Procedure
            if (actual instanceof UnitType) {
                return new VoidType();
            } else if (actual instanceof VariableType) {
                if (((VariableType) actual).equals((VariableType) expected)) {
                    return new VoidType();
                } else {
                    throw new TypeErrorException(expected, actual);
                }
            } else { // ListVariableType
                if (((ListVariableType) actual).equals((ListVariableType) expected)) {
                    return new VoidType();
                } else {
                    throw new TypeErrorException(expected, actual);
                }
            }
        }
        
    }
}
