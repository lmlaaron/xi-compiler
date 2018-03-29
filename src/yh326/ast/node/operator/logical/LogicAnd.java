package yh326.ast.node.operator.logical;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import yh326.util.NumberGetter;

public class LogicAnd extends LogicalOperator {
    public LogicAnd(int line, int col) {
        super(line, col, "&");
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
        String labelNumber = NumberGetter.uniqueNumber();

        List<IRStmt> stmts = new ArrayList<IRStmt>();
        IRTemp result = new IRTemp("_and_" + labelNumber);
        IRMove storeFalse = new IRMove(result, new IRConst(0));
        String l1Name = "_l1_" + labelNumber;
        String l2Name = "_l2_" + labelNumber;
        String lfName = "_lf_" + labelNumber;
        IRLabel l1 = new IRLabel(l1Name);
        IRLabel l2 = new IRLabel(l2Name);
        IRLabel lf = new IRLabel(lfName);
        IRCJump ircJump1 = new IRCJump((IRExpr) operands[0], l1Name, lfName);
        IRCJump ircJump2 = new IRCJump((IRExpr) operands[1], l2Name, lfName);
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
