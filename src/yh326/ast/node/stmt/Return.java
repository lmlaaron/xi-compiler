package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.type.NodeType;

public class Return extends Stmt {

    public Return(int line, int col) {
        super(line, col, new Keyword(line, col, "return"));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        throw new RuntimeException("Not implemented!!!");
    }
}
