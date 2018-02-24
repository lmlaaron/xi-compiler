package yh326.ast.node.stmt;

import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.UnitNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.TypeErrorException;

public class Empty extends Stmt {

    public Empty(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

    public NodeType typeCheck() throws TypeErrorException {
        if (this.value=="{}") {
            return new UnitNodeType();
        }
        return null; // TODO to avoid error
    }
}