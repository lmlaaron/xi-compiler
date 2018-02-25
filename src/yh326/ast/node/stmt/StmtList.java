package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitNodeType;

public class StmtList extends Stmt {

    public StmtList(int line, int col, Stmt stmt) {
        super(line, col, stmt);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.enterBlock();
        NodeType type = new UnitNodeType();
        if (children != null) {
            for (Node child : children) {
                if (child != null) {
                    type = child.typeCheck(sTable);
                }
            }
        }
        sTable.exitBlock();
        return type;
    }

}
