package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Subscript;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Get;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Underscore;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.exception.AssignTypeException;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class AssignSingle extends Stmt {
    private Node lhs;
    private Expr expr;

    public AssignSingle(int line, int col, Node lhs, Expr expr) {
        super(line, col, new Get(line, col), lhs, expr);
        this.setLhs(lhs);
        this.setExpr(expr);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        return;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType rightType = getExpr().typeCheck(sTable);

        // Single assign on LHS, including assigning to subscript.
        VariableType leftType = getLhsType(sTable, getLhs());
        if (rightType instanceof VariableType && leftType.equals((VariableType) rightType)) {
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
    private VariableType getLhsType(SymbolTable sTable, Node lhs) throws Exception {
        // If LHS is underscore, don't need to check
        // TODO: this is actually not following the type system, where are are asked
        // to return UnitType for underscore.
        if (lhs instanceof Underscore) {
            return new PrimitiveType(Primitives.ANY);
        }

        VariableType leftType = null;
        if (lhs instanceof VarDecl) { // VarInit in the Xi type system
            leftType = (VariableType) ((VarDecl) lhs).typeCheckAndReturn(sTable);
        } else {
            leftType = (VariableType) lhs.typeCheck(sTable);
        }
        return leftType;
    }

    @Override
    public IRNode translate() {
        if (getLhs() instanceof Underscore) {
            return new IRExp((IRExpr) getExpr().translate());
        } else {
            return new IRMove((IRExpr) getLhs().translate(), (IRExpr) getExpr().translate());
        }
    }

	public Node getLhs() {
		return lhs;
	}

	public void setLhs(Node lhs) {
		this.lhs = lhs;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}
}
