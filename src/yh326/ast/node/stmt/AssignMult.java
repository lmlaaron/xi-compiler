package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.node.Underscore;
import yh326.ast.node.expr.Expr;
import yh326.ast.node.Get;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.AssignTypeException;

public class AssignMult extends Stmt {
    private Node lhs;
    private Expr expr;

    public AssignMult(int line, int col, AssignToList lhs, Expr expr) {
        super(line, col, new Get(line, col), lhs, expr);
        this.lhs = lhs;
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType rightType = expr.typeCheck(sTable);
        
        // Multi-assign: LHS can only be comma separated varDecl or underscores.
        List<VariableType> types = new ArrayList<VariableType>();
        for (Node child : lhs.children) {
        	if (child instanceof Underscore) {
        		types.add(new VariableType(Primitives.ANY));
        	} else if (child instanceof VarDecl) {
        		types.add((VariableType) ((VarDecl) child).typeCheckAndReturn(sTable));
        	}
            
        }
        
        // Check if LHS and RHS match.
        ListVariableType leftType = new ListVariableType(types);
        if (leftType.equals((ListVariableType) rightType)) {
            return new UnitType();
        } else {
            throw new AssignTypeException(line, col, rightType, leftType);
        }
        
    }
}
