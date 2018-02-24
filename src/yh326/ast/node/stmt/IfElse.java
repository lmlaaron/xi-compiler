package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

import java.util.ArrayList;

public class IfElse extends Stmt {
    protected Expr guard;
    protected Stmt consequent;
    protected Stmt alternative;

    public IfElse(int line, int col, Node... nodes) {
        super(line, col, nodes);
        if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt && nodes[2] instanceof Stmt) {
            guard = (Expr)nodes[0];
            consequent = (Stmt)nodes[1];
            alternative = (Stmt)nodes[2];
        }
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType tg = guard.typeCheck(st);
        NodeType boolType = new VariableNodeType(PrimitiveNodeType.BOOL);
        if (!tg.equals(boolType)) {
            throw new TypeErrorException(boolType, tg);
        }
        NodeType tc = consequent.typeCheck(st);
        NodeType ta = alternative.typeCheck(st);
        return VariableNodeType.Lub(tc, ta); // TODO: what is Lub????
    }
}