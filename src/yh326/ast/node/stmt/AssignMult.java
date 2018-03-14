package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;
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
    private AssignToList lhs;
    private Expr expr;

    public AssignMult(int line, int col, AssignToList lhs, Expr expr) {
        super(line, col, new Get(line, col), lhs, expr);
        this.lhs = lhs;
        this.expr = expr;
    }

    @Override
    public IRNode translate() {
        List<IRStmt> translation = new ArrayList<>();

        // call function first
        translation.add(
                new IRExp( (IRExpr) expr.translate() )
        );

        // move return values into lhs
        for (int i = 0; i < lhs.children.size(); i++) {
            translation.add(
                new IRMove(
                    (IRExpr) lhs.children.get(i).translate(),
                    new IRTemp("__RET" + i) //TODO: don't know if this is correct naming convention
                )
            );
        }

        return new IRSeq(
            translation
        );
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
