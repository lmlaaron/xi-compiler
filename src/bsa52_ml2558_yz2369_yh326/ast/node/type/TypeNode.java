package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;

public class TypeNode extends Node {

    public TypeNode(int line, int col, String string) {
        super(line, col, string);
    }

    public TypeNode(int line, int col, Node... nodes) {
        super(line, col, nodes);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = (VariableType) children.get(1).typeCheck(sTable);

        if (children.size() <= 2) {
            // Size of array not given
            t.increaseLevel();
        } else {
            // Size of array given. Check if size (expr) is integer.
            PrimitiveType expr = (PrimitiveType) children.get(2).typeCheck(sTable);
            PrimitiveType integer = new PrimitiveType(Primitives.INT);
            if (expr.equals(integer)) {
                t.increaseLevel((Expr) children.get(2));
            } else {
                throw new MatchTypeException(line, col, integer, expr);
            }
        }

        return t;
    }
}
