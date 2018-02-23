package yh326.ast.node.stmt;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.NodeDecoration;
import yh326.ast.type.NodeType;
import yh326.exception.TypeErrorException;

import java.util.ArrayList;
import java.util.List;

public class Seq extends Stmt {
    protected List<Stmt> stmt_seq;


    public Seq(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType retT = null;
        for (Node node : this.children) {
            retT = node.typeCheck(st);
        }
        return retT;
    }

}