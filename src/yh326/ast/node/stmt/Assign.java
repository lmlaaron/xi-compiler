package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Node;
import yh326.ast.node.Underscore;
import yh326.ast.node.expr.Expr;
import yh326.ast.node.expr.MethodCall;
import yh326.ast.node.expr.Subscript;
import yh326.ast.node.Get;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.AssignTypeException;
import yh326.exception.OtherException;

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
                types.add(getLhsType(sTable, child, true));
            }
            ListVariableType leftType = new ListVariableType(types);
            
            // RHS is ListVarType means RHS must be a method call that returns
            // multiple results. It cannot be anything other expression.
            if (rightType instanceof ListVariableType &&
                    (leftType).equals((ListVariableType) rightType)) {
                return new UnitType();
            } else {
                throw new AssignTypeException(line, col, rightType, leftType);
            }
        } else {
            // Single assign on LHS, including assigning to subscript.
            VariableType leftType = getLhsType(sTable, lhs, false);
            if (leftType.getType() == Primitives.ANY &&
                    !(expr instanceof MethodCall)) {
                throw new OtherException(line, col, "Expected function call");
            }
            if (rightType instanceof VariableType &&
                    (leftType).equals((VariableType) rightType)) {
                return new UnitType();
            } else {
                throw new AssignTypeException(line, col, rightType, leftType);
            }
        }
    }

    /**
     * @param sTable
     * @param lhs
     * @param declOnly 
     * @return
     * @throws Exception
     */
    private VariableType getLhsType(SymbolTable sTable, Node lhs, boolean declOnly)
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
        }
        
        if (!declOnly) {
            if (lhs instanceof Identifier) {       // Assign in the Xi type system
                leftType = (VariableType) ((Identifier) lhs).typeCheck(sTable);
            } else if (lhs instanceof Subscript) { // ArrAssign in the Xi type system
                leftType = (VariableType) ((Subscript) lhs).typeCheck(sTable);
            }
        }
        if (leftType == null) {
            throw new OtherException(line, col, "LHS of multi-assign can only be vardecl or _");
        }
        return leftType;
    }
}
