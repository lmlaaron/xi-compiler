package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.exception.MatchTypeException;

public class StmtList extends Stmt {

    public StmtList(int line, int col) {
        super(line, col);
    }
    
    public StmtList(int line, int col, Stmt stmt) {
        super(line, col, stmt);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.enterBlock();
        NodeType type = new UnitType();
        
        // Check if all statement except for the last one has unit type.
        for (int i = 0; i < children.size() - 1; i++) {
            NodeType actual = children.get(i).typeCheck(sTable);
            if (!(actual instanceof UnitType)) {
                throw new MatchTypeException(line, col, type, actual);
            }
        }
        
        // Use the last statement's type as the return value.
        if (children.size() > 0) {
            type = children.get(children.size() - 1).typeCheck(sTable);
        }
        sTable.exitBlock();
        return type;
    }

}
