package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.Expr;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

import java.util.ArrayList;

public class IFELSE extends Stmt {
    protected Expr guard;
    protected Stmt consequent;
    protected Stmt alternative;

    public IFELSE(Node... nodes) {
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
        if (nodes[0] instanceof Expr && nodes[1] instanceof Stmt && nodes[2] instanceof Stmt) {
            guard = (Expr)nodes[0];
            consequent = (Stmt)nodes[1];
            alternative = (Stmt)nodes[2];
        }
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws TypeErrorException {
        NodeType tg = guard.typeCheck(st);
        NodeType boolType = new VariableNodeType(PrimitiveNodeType.BOOL);
        if (!tg.equals(boolType)) {
            throw new TypeErrorException(boolType, tg);
        }
        NodeType tc = consequent.typeCheck(st);
        NodeType ta = alternative.typeCheck(st);
        return new NodeType.Lub(tc, ta); // TODO: what is Lub????
    }
}