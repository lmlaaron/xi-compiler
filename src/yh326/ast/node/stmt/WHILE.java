package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.Expr;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.node.stmt.Stmt;
import yh326.ast.type.PrimitiveType;
import yh326.ast.type.Type;
import yh326.ast.type.VariableType;

import java.util.ArrayList;

public class WHILE extends Stmt {
    protected Expr guard;
    protected Stmt consequent;

    public WHILE(Node... nodes) {
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
        if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt) {
            guard = (Expr) nodes[0];
            consequent = (Stmt) nodes[1];
        }
    }

    @Override
    public Type typeCheck(SymbolTable st) throws TypeErrorException {
        Type tg = guard.typeCheck(st);
        Type boolType = new VariableType(PrimitiveType.BOOL);
        if (!tg.equals(boolType)) {
            throw new TypeErrorException(boolType, tg);
        }
        Type tc = consequent.typeCheck(st);
        return new VariableType(PrimitiveType.UNIT);
    }
}