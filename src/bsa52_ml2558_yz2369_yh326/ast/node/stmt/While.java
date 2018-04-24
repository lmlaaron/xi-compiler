package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

public class While extends Stmt {
    protected Expr condition;
    protected Stmt then;

    public While(int line, int col, Expr condition, Stmt then) {
        super(line, col, new Keyword(line, col, "while"), condition, then);
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
    public IRNode translate() {
        return getIRWhile((IRExpr) condition.translate(), then.translate());
    }

    public static IRSeq getIRWhile(IRExpr cond, IRNode then) {
        String labelNumber = NumberGetter.uniqueNumberStr();

        List<IRStmt> stmts = new ArrayList<IRStmt>();
        stmts.add(new IRLabel("_head_" + labelNumber));
        stmts.add(new IRCJump(cond, "_then_" + labelNumber));
        stmts.add(new IRJump(new IRName("_end_" + labelNumber)));
        stmts.add(new IRLabel("_then_" + labelNumber));
        if (then instanceof IRExpr) {
            stmts.add(new IRExp((IRExpr) then));
        } else {
            stmts.add((IRStmt) then);
        }
        stmts.add(new IRJump(new IRName("_head_" + labelNumber)));
        stmts.add(new IRLabel("_end_" + labelNumber));
        return new IRSeq(stmts);
    }
}