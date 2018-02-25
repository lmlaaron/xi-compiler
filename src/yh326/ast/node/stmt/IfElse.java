package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;

public class IfElse extends Stmt {
    protected Expr condition;
    protected Stmt then;
    protected Stmt otherwise;

    public IfElse(int line, int col, Expr condition, Stmt then, Stmt otherwise) {
        super(line, col, new Keyword(line, col, "if"), condition, then, otherwise);
        this.condition = condition;
        this.then = then;
        this.otherwise = otherwise;
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType tg = condition.typeCheck(st);
        NodeType boolType = new VariableType(Primitives.BOOL);
        if (!tg.equals(boolType)) {
            throw new TypeErrorException(boolType, tg);
        }
        NodeType tc = then.typeCheck(st);
        NodeType ta = otherwise.typeCheck(st);
        return Lub(tc, ta);
    }
    
    /**
     * @param other
     * @return unit type if a or b is unit type
     * @throws exception
     **/
    public static NodeType Lub(NodeType a, NodeType b) throws TypeErrorException {
        if (a.equals(b)) {
            return a;
        } else if (a instanceof UnitType) {
            return a;
        } else if (b instanceof UnitType) {
            return b;
        } else {
            throw new TypeErrorException(a, b); // TODO Wrong use of the Exception
        }
    }    
}