package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Get;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Underscore;
import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AssignTypeException;
import edu.cornell.cs.cs4120.xic.ir.*;

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
        List<IRStmt> stmts = new ArrayList<>();

        // call function first
        stmts.add(new IRExp((IRExpr) expr.translate()));

        // move return values into lhs
        for (int i = 0; i < lhs.children.size(); i++) {
            if (!(lhs.children.get(i) instanceof Underscore)) {
                stmts.add(new IRMove((IRExpr) lhs.children.get(i).translate(), new IRTemp("_RET" + i)));
            }
        }

        return new IRSeq(stmts);
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
