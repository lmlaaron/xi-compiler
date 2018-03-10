package yh326.ast.node.operator.logical;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;

public class LogicAnd extends LogicalOperator {
    public LogicAnd(int line, int col) {
        super(line, col, "&");
    }
    
    @Override
	public IRNode translate() {
		List<IRStmt> stmts = new ArrayList<IRStmt> ();
		IRTemp result = new IRTemp("x");
		IRMove storeFalse = new IRMove(result, new IRConst(0));
		String l1Name = "l1_" + "L" + line + "C" + col;
		String l2Name = "l2_" + "L" + line + "C" + col;
		String lfName = "lf_" + "L" + line + "C" + col;
		IRLabel l1 = new IRLabel(l1Name);
		IRLabel l2 = new IRLabel(l2Name);
		IRLabel lf = new IRLabel(lfName);
		IRCJump ircJump1 = new IRCJump((IRExpr) children.get(1).translate(), l1Name, l2Name);
		IRCJump ircJump2 = new IRCJump((IRExpr) children.get(2).translate(), l2Name, lfName);
		IRMove storeTrue = new IRMove(result, new IRConst(1));
		stmts.add(storeFalse);
		stmts.add(ircJump1);
		stmts.add(l1);
		stmts.add(ircJump2);
		stmts.add(l2);
		stmts.add(storeTrue);
		stmts.add(lf);
		IRSeq irSeq = new IRSeq(stmts);
		return new IRESeq(irSeq, result);
	}
}
