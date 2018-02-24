package yh326.ast.node.assign;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.Underscore;
import yh326.ast.node.expr.Expr;
import yh326.ast.node.stmt.Stmt;
import yh326.ast.node.stmt.VarDecl;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitNodeType;
import yh326.exception.TypeErrorException;

public class Assign extends Stmt {
    private Node lhs;
    private Expr expr;

    public Assign(int line, int col, Node lhs, Expr expr) {
        super(line, col, lhs, expr);
        this.lhs = lhs;
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // If LHS is underscore, don't need to check
        if (lhs instanceof Underscore) {
            return new UnitNodeType();
        }
        
        sTable.enterBlock();
        NodeType leftType;
        if (lhs instanceof Identifier) {
            leftType = ((Identifier) lhs).typeCheck(sTable);
        } else if (lhs instanceof VarDecl) {
            leftType = ((VarDecl) lhs).typeCheck(sTable);
        }/* else if (lhs instanceof Subscript) {
            leftType = ((Subscript) lhs).typeCheck(sTable);
        }*/ else {
            throw new RuntimeException("Unexpected Error.");
        }
        NodeType rightType = expr.typeCheck(sTable);
        sTable.exitBlock();
        if (leftType.equals(rightType)) {
            return new UnitNodeType();
        } else {
            throw new TypeErrorException("Cannot assign " + rightType + " to " + leftType + ".");
        }
    }
}
