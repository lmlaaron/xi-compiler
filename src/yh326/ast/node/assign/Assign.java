package yh326.ast.node.assign;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.Underscore;
import yh326.ast.node.expr.Expr;
import yh326.ast.node.operator.Get;
import yh326.ast.node.stmt.Stmt;
import yh326.ast.node.stmt.VarDecl;
import yh326.ast.node.type.Subscript;
import yh326.ast.node.type.TypeNode;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.TypeErrorException;

public class Assign extends Stmt {
    private Node lhs;
    private Expr expr;

    public Assign(int line, int col, Node lhs, Expr expr) {
        super(line, col, new Get(line, col), lhs, expr);
        this.lhs = lhs;
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType rightType = expr.typeCheck(sTable);
        if (lhs instanceof AssignToList) {
            List<VariableType> types = new ArrayList<VariableType>();
            for (Node child : lhs.children) {
                types.add(getLhsType(sTable, child));
            }
            ListVariableType leftType = new ListVariableType(types);
            if (rightType instanceof ListVariableType &&
                    (leftType).equals((ListVariableType) rightType)) {
                return new UnitType();
            } else {
                throw new TypeErrorException("Cannot assign " + rightType + " to " + leftType + ".");
            }
        } else {
            VariableType leftType = getLhsType(sTable, lhs);
            if (rightType instanceof VariableType &&
                    (leftType).equals((VariableType) rightType)) {
                return new UnitType();
            } else {
                throw new TypeErrorException("Cannot assign " + rightType + " to " + leftType + ".");
            }
        }
    }

    private VariableType getLhsType(SymbolTable sTable, Node lhs) throws Exception {
        // If LHS is underscore, don't need to check
        if (lhs instanceof Underscore) {
            return new VariableType(Primitives.ANY);
        }
        
        VariableType leftType = null;
        if (lhs instanceof Identifier) {
            leftType = (VariableType) ((Identifier) lhs).typeCheck(sTable);
        } else if (lhs instanceof VarDecl) {
            leftType = (VariableType) ((VarDecl) lhs).typeCheck(sTable);
        } else if (lhs instanceof Subscript) {
            leftType = (VariableType) ((Subscript) lhs).typeCheck(sTable);
        }
        if (leftType == null) {
            throw new RuntimeException("Unexpected Error.");
        }
        return leftType;
    }
}
