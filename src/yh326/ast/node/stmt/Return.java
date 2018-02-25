package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;

public class Return extends Stmt {

    public Return(int line, int col) {
        super(line, col, new Keyword(line, col, "return"));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        if (children.size() == 1) {
            return new UnitType();
        } else if (children.size() == 2) {
            return (VariableType) children.get(1).typeCheck(sTable);
        } else {
            List<VariableType> listType = new ArrayList<VariableType>();
            for (int i = 1; i < children.size(); i++) {
                listType.add((VariableType) children.get(i).typeCheck(sTable));
            }
            return new ListVariableType(listType);
        }
    }
}
