package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;

public class If extends Stmt {
    protected Expr condition;
    protected Stmt then;

    public If(int line, int col, Expr condition, Stmt then) {
        super(line, col, new Keyword(line, col, "if"), condition, then);
        this.condition = condition;
        this.then = then;
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType tg = condition.typeCheck(st);
        NodeType boolType = new VariableType(Primitives.BOOL);
        
        if (!tg.equals(boolType)) {
            throw new TypeErrorException(boolType, tg);
        }
        then.typeCheck(st);
        return new UnitType();
    }
}