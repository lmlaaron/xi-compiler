package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.node.expr.Expr;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.MatchTypeException;
import yh326.util.NumberGetter;

public class If extends Stmt {
    protected Expr condition;
    protected Stmt then;

    public If(int line, int col, Expr condition, Stmt then) {
        super(line, col, new Keyword(line, col, "if"), condition, then);
        this.condition = condition;
        this.then = then;
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType tg = condition.typeCheck(st);
        NodeType boolType = new VariableType(Primitives.BOOL);

        if (!tg.equals(boolType)) {
            throw new MatchTypeException(line, col, boolType, tg);
        }

        st.enterBlock();
        then.typeCheck(st);
        st.exitBlock();

        return new UnitType();
    }

    @Override
    // TODO: need to run insnMapsBuilder to construct the mapping from "then" to
    // isTrueLabel
    public IRNode translate() {
        return getIRIf((IRExpr) condition.translate(), then.translate());
    }

    public static IRSeq getIRIf(IRExpr cond, IRNode then) {
        String labelNumber = NumberGetter.uniqueNumber();

        List<IRStmt> stmts = new ArrayList<IRStmt>();
        stmts.add(new IRCJump(cond, "_then_" + labelNumber));
        stmts.add(new IRJump(new IRName("_end_" + labelNumber)));
        stmts.add(new IRLabel("_then_" + labelNumber));
        IRNode thenStmt = then;
        if (thenStmt instanceof IRExpr) {
            stmts.add(new IRExp((IRExpr) thenStmt));
        } else {
            stmts.add((IRStmt) thenStmt);
        }
        stmts.add(new IRLabel("_end_" + labelNumber));
        return new IRSeq(stmts);
    }
}