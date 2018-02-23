package yh326.ast.node.stmt;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.NodeType;
import yh326.ast.type.PrimitiveNodeType;
import yh326.ast.type.VariableNodeType;

public class EMPTY extends Stmt {

    public NodeType typeCheck() throws TypeErrorException {
        if (this.value=="{}") {
            return new VariableNodeType(PrimitiveNodeType.UNIT);
        }
    }
}