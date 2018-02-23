package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.exception.TypeErrorException;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.type.Type;

import java.util.ArrayList;
import java.util.List;

public class SEQ extends Stmt {
    protected List<Stmt> stmt_seq;


    public SEQ(Node... nodes) {
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }

    @Override
    public Type typeCheck(SymbolTable st) throws TypeErrorException {
        Type retT = null;
        for (Node node : this.children) {
            retT = node.typeCheck(st);
        }
        return retT;
    }

}