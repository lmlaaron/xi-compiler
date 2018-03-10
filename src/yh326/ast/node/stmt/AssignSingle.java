package yh326.ast.node.stmt;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.Underscore;
import yh326.ast.node.expr.Expr;
import yh326.ast.node.expr.Subscript;
import yh326.ast.node.Get;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.AssignTypeException;

public class AssignSingle extends Stmt {
    private Node lhs;
    private Expr expr;

    public AssignSingle(int line, int col, Node lhs, Expr expr) {
        super(line, col, new Get(line, col), lhs, expr);
        this.lhs = lhs;
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType rightType = expr.typeCheck(sTable);
    
        // Single assign on LHS, including assigning to subscript.
        VariableType leftType = getLhsType(sTable, lhs);
        if (rightType instanceof VariableType &&
                leftType.equals((VariableType) rightType)) {
            return new UnitType();
        } else {
            throw new AssignTypeException(line, col, rightType, leftType);
        }
        
    }

    /**
     * @param sTable
     * @param lhs
     * @param multiAssign 
     * @return
     * @throws Exception
     */
    private VariableType getLhsType(SymbolTable sTable, Node lhs)
            throws Exception {
        // If LHS is underscore, don't need to check
        // TODO: this is actually not following the type system, where are are asked
        // to return UnitType for underscore.
        if (lhs instanceof Underscore) {
            return new VariableType(Primitives.ANY);
        }
        
        VariableType leftType = null;
        if (lhs instanceof VarDecl) {   // VarInit in the Xi type system
            leftType = (VariableType) ((VarDecl) lhs).typeCheckAndReturn(sTable);
        } else if (lhs instanceof Identifier) {       // Assign in the Xi type system
            leftType = (VariableType) ((Identifier) lhs).typeCheck(sTable);
        } else if (lhs instanceof Subscript) { // ArrAssign in the Xi type system
            leftType = (VariableType) ((Subscript) lhs).typeCheck(sTable);
        }
        return leftType;
    }
    
    @Override
    public IRNode translate() {
    	return new IRMove((IRExpr) lhs.translate(), (IRExpr) expr.translate());
    }
}
