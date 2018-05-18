package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.*;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.*;

public class Foreach extends Loop {
    protected Identifier gets;
    protected Identifier from;
    protected Stmt then;

    public Foreach(int line, int col, Identifier gets, Identifier from, Stmt then) {
        super(line, col, new Keyword(line, col, "for"), gets, new Keyword(line, col, "in"), from, then);
        this.gets = gets;
        this.from = from;
        this.then = then;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {

        // 'from' has to be an array
        NodeType fromType = from.typeCheck(sTable);
        if (!(fromType instanceof VariableType)) {
            throw new MatchTypeException(line, col, "Any Array Type", fromType);
        }
        else {

            VariableType fromTypevt = (VariableType)fromType;

            if (fromTypevt.getLevel() < 1) {
                throw new MatchTypeException(line, col, "Must have level >1", fromTypevt);
            }

            sTable.enterBlock();
            sTable.enterLoop(this);

            // add loop variable to typing context:
            VariableType getsType = fromTypevt.copy();
            getsType.decreaseLevel();
            sTable.addVar(gets.value, getsType);

            then.typeCheck(sTable);

            sTable.exitLoop();
            sTable.exitBlock();

            return new UnitType();
        }
    }

    @Override
    public IRNode translate() {
        labelNumber = NumberGetter.uniqueNumberStr();

        List<IRStmt> stmts = new ArrayList<IRStmt>();

        // variable to track index
        IRTemp index = new IRTemp(Utilities.freshTemp());
        stmts.add(new IRMove(index, new IRConst(0)));

        IRTemp arr = new IRTemp(from.value);

        IRTemp arrlen = new IRTemp(Utilities.freshTemp());
        stmts.add(new IRMove(arrlen, new IRMem(new IRBinOp(IRBinOp.OpType.SUB, arr, new IRConst(8)))));

        IRExpr condition = new IRBinOp(IRBinOp.OpType.LT, index, arrlen);

        stmts.add(new IRLabel("_head_" + labelNumber));
        stmts.add(new IRCJump(condition, "_then_" + labelNumber));
        stmts.add(new IRJump(new IRName("_end_" + labelNumber)));
        stmts.add(new IRLabel("_then_" + labelNumber));
        // first thing to do in the loop is to assign to 'gets'
        stmts.add(new IRMove(new IRTemp(gets.value),
                             new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                                   arr,
                                                   new IRBinOp(IRBinOp.OpType.MUL,
                                                               index,
                                                               new IRConst(8))))));
        // the second thing is to incremeånt the index:
        stmts.add(new IRMove(index, new IRBinOp(IRBinOp.OpType.ADD, new IRConst(1), index)));
        // the third thing is whatever the loop is meant to do
        IRNode then = this.then.translate();
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