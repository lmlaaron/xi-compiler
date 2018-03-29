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
import yh326.ast.type.VoidType;
import yh326.exception.MatchTypeException;
import yh326.exception.TypeInconsistentException;
import yh326.util.NumberGetter;

public class IfElse extends Stmt {
    protected Expr condition;
    protected Stmt then;
    protected Stmt otherwise;

    public IfElse(int line, int col, Expr condition, Stmt then, Stmt otherwise) {
        super(line, col, new Keyword(line, col, "if"), condition, then, otherwise);
        this.condition = condition;
        this.then = then;
        this.otherwise = otherwise;
    }

    @Override
    public NodeType typeCheck(SymbolTable st) throws Exception {
        NodeType tg = condition.typeCheck(st);
        NodeType boolType = new VariableType(Primitives.BOOL);
        if (!tg.equals(boolType)) {
            throw new MatchTypeException(line, col, boolType, tg);
        }
        st.enterBlock();
        NodeType tc = then.typeCheck(st);
        st.exitBlock();

        st.enterBlock();
        NodeType ta = otherwise.typeCheck(st);
        st.exitBlock();

        NodeType result = Lub(tc, ta);
        if (result == null) {
            throw new TypeInconsistentException(line, col, "If-else return");
        } else {
            return result;
        }
    }

    /**
     * @param other
     * @return unit type if a or b is unit type
     * @throws exception
     **/
    public static NodeType Lub(NodeType a, NodeType b) {
        if (a instanceof UnitType || b instanceof UnitType) {
            return new UnitType();
        } else if (a instanceof VoidType && b instanceof VoidType) {
            return new VoidType();
        } else {
            return null;
        }
    }

    // TODO: need to run InsnMapsBuiilder to construct the mapping from irName to
    // headLabel
    // TODO: need to run InsnMapsBuilder to construct the mapping from "head" to
    // headLabel
    @Override
    public IRNode translate() {
        String labelNumber = NumberGetter.uniqueNumber();

        List<IRStmt> stmts = new ArrayList<IRStmt>();
        stmts.add(new IRCJump((IRExpr) condition.translate(), "_then_" + labelNumber));
        IRNode otherwiseStmt = otherwise.translate();
        if (otherwiseStmt instanceof IRExpr) {
            stmts.add(new IRExp((IRExpr) otherwiseStmt));
        } else {
            stmts.add((IRStmt) otherwiseStmt);
        }
        stmts.add(new IRJump(new IRName("_end_" + labelNumber)));
        stmts.add(new IRLabel("_then_" + labelNumber));
        IRNode thenStmt = then.translate();
        if (thenStmt instanceof IRExpr) {
            stmts.add(new IRExp((IRExpr) thenStmt));
        } else {
            stmts.add((IRStmt) thenStmt);
        }
        stmts.add(new IRLabel("_end_" + labelNumber));
        return new IRSeq(stmts);
    }
}