package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.*;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.*;
import edu.cornell.cs.cs4120.xic.ir.visit.*;

public class Foreach extends Stmt {
    protected Identifier gets;
    protected Identifier from;
    protected Stmt then;
    public String labelNumber;

    public Foreach(int line, int col, Identifier gets, Identifier from, Stmt then) {
        super(line, col, new Keyword(line, col, "for"), then);
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

        sTable.enterBlock();
        sTable.enterLoop(this);

        // add loop variable to typing context:
        VariableType getsType = ((VariableType) fromType).copy();
        getsType.decreaseLevel();
        sTable.addVar(gets.value, getsType);

        then.typeCheck(sTable);

        sTable.exitLoop();
        sTable.exitBlock();

        return new UnitType();
    }

    @Override
    public IRNode translate() {
        return getIRForeach(this);
    }

    public static IRSeq getIRForeach(Foreach f) {
        String labelNumber = NumberGetter.uniqueNumberStr();

        List<IRStmt> stmts = new ArrayList<IRStmt>();

        // variable to track index
        IRTemp index = new IRTemp(Utilities.freshTemp());
        stmts.add(new IRMove(index, new IRConst(0)));

        IRTemp arr = new IRTemp(f.from.value);

        IRTemp arrlen = new IRTemp(Utilities.freshTemp());
        stmts.add(new IRMove(arrlen, new IRMem(new IRBinOp(IRBinOp.OpType.SUB, arr, new IRConst(8)))));

        IRExpr condition = new IRBinOp(IRBinOp.OpType.LT, index, arrlen);

        stmts.add(new IRLabel("_head_" + labelNumber));
        stmts.add(new IRCJump(condition, "_then_" + labelNumber));
        stmts.add(new IRJump(new IRName("_end_" + labelNumber)));
        stmts.add(new IRLabel("_then_" + labelNumber));
        // first thing to do in the loop is to assign to 'gets'
        stmts.add(new IRMove(new IRTemp(f.gets.value),
                             new IRMem(new IRBinOp(IRBinOp.OpType.ADD,
                                                   arr,
                                                   new IRBinOp(IRBinOp.OpType.MUL,
                                                               index,
                                                               new IRConst(8))))));
        // the second thing is to increment the index:
        stmts.add(new IRMove(index, new IRBinOp(IRBinOp.OpType.ADD, new IRConst(1), index)));
        // the third thing is whatever the loop is meant to do
        IRNode then = f.then.translate();
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