package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class Length extends Expr {
    private Expr expr;

    public Length(int line, int col, Expr expr) {
        super(line, col, new Keyword(line, col, "length"), expr);
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableNodeType type = (VariableNodeType) expr.typeCheck(sTable);
        if (type.getLevel() >= 1) {
            return new VariableNodeType(PrimitiveNodeType.INT);
        } else {
            throw new TypeErrorException(expr + " should be an array.");
        }
    }
}
