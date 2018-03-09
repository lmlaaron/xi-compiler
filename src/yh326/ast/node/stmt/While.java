package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRCJump;
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
import yh326.ast.type.Primitives;
import yh326.ast.type.UnitType;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableType;
import yh326.exception.MatchTypeException;

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
    	List<IRStmt> stmts = new ArrayList<IRStmt> ();
    	String headName = "while_head_" + "L" + line + "C" + col;
    	String trueName = "while_true_" + "L" + line + "C" + col;
    	String falseName = "while_false" + "L" + line + "C" + col;
    	IRLabel headLabel = new IRLabel(headName);
    	IRLabel trueLabel = new IRLabel(trueName);
    	IRLabel falseLabel = new IRLabel(falseName);
    	IRCJump irCJump = new IRCJump((IRExpr) condition.translate(), trueName, falseName);
    	IRStmt thenStmt = (IRStmt) then.translate();
    	IRJump jump = new IRJump(new IRName(headName));
    	stmts.add(headLabel);
    	stmts.add(irCJump);
    	stmts.add(trueLabel);
    	stmts.add(thenStmt);
    	stmts.add(jump);
    	stmts.add(falseLabel);
    	return new IRSeq(stmts);
    }
}