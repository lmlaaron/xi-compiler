package bsa52_ml2558_yz2369_yh326.ir;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.exception.IRNodeNotMatchException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class Canonicalization {
    /**
     * canonicalize IRNode (which can be IRExpr, IRStmt, IRFuncDecl, IRCompUnit),
     * 
     * @return: IRNode tree with all non-leaf node as SEQ or ESEQ
     */
    public static IRNode Canonicalize(IRNode input) throws IRNodeNotMatchException {
        try {
            if (input instanceof IRExpr) {
                return CanonicalizeExpr((IRExpr) input);
            } else if (input instanceof IRStmt) {
                return CanonicalizeStmt((IRStmt) input);
            } else if (input instanceof IRFuncDecl) {
                return new IRFuncDecl(((IRFuncDecl) input).name(), CanonicalizeStmt(((IRFuncDecl) input).body()));
            } else if (input instanceof IRCompUnit) {
                Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
                for (Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet()) {
                    functions.put(function.getKey(), (IRFuncDecl) Canonicalize(function.getValue()));
                }
                return new IRCompUnit(((IRCompUnit) input).name(), functions, (IRCompUnit) input);
            } else {
                throw new IRNodeNotMatchException(input);
            }
        } catch (IRNodeNotMatchException e) {
            throw new IRNodeNotMatchException(input);
        }
    }

    /**
     * 
     * @param input
     *            irNode
     * @return irNode with blocks reordered
     */
    static IRNode BlockReordering(IRNode input) {
        if (input instanceof IRSeq) {
            BasicBlocks blocks = new BasicBlocks(((IRSeq) input).stmts());
            return new IRSeq(blocks.GenTrace());
        } else if (input instanceof IRCompUnit) {
            Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
            for (Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet()) {
                functions.put(function.getKey(), (IRFuncDecl) BlockReordering(function.getValue()));
            }
            return new IRCompUnit(((IRCompUnit) input).name(), functions,  (IRCompUnit) input);
        } else if (input instanceof IRFuncDecl) {
            return new IRFuncDecl(((IRFuncDecl) input).name(), (IRStmt) BlockReordering(((IRFuncDecl) input).body()));
        } else {
            return input;
        }
    }

    static IRNode TameCjump(IRNode input) {
        if (input instanceof IRSeq) {
            List<IRStmt> stmts = ((IRSeq) input).stmts();
            List<IRStmt> results = new ArrayList<IRStmt>();
            for (IRStmt stmt : stmts) {
                if (stmt instanceof IRCJump && ((IRCJump) stmt).hasFalseLabel()) {
                    IRLabel label = new IRLabel("_temp_" + NumberGetter.uniqueNumber());
                    results.add(new IRCJump(((IRCJump) stmt).cond(), ((IRCJump) stmt).trueLabel(), null));
                    results.add(label);
                    results.add(new IRJump(new IRName(((IRCJump) stmt).falseLabel())));
                } else {
                    if (stmt != null)
                        results.add(stmt);
                }
            }
            return new IRSeq(results);
        } else if (input instanceof IRESeq) {
            return input;
        } else if (input instanceof IRStmt) {
            return input;
        } else if (input instanceof IRExpr) {
            return input;
        } else if (input instanceof IRFuncDecl) {
            return new IRFuncDecl(((IRFuncDecl) input).name(), (IRStmt) TameCjump(((IRFuncDecl) input).body()));
        } else if (input instanceof IRCompUnit) {
            Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
            for (Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet()) {
                functions.put(function.getKey(), (IRFuncDecl) TameCjump(function.getValue()));
            }
            return new IRCompUnit(((IRCompUnit) input).name(), functions,  (IRCompUnit) input);
        }
        return input;
    }

    /**
     * Canonicalize will turn all non-leaf node IRSeq or IRESeq, lift will lift all
     * these nodes to the top
     */
    static IRNode Lift(IRNode input) {
        if (input instanceof IRSeq) {
            return new IRSeq(LiftSeq((IRStmt) input));
        } else if (input instanceof IRESeq) {
            return input;
        } else if (input instanceof IRStmt) {
            return input;
        } else if (input instanceof IRExpr) {
            return input;
        } else if (input instanceof IRFuncDecl) {
            return new IRFuncDecl(((IRFuncDecl) input).name(), (IRStmt) Lift(((IRFuncDecl) input).body()));
        } else if (input instanceof IRCompUnit) {
            Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
            for (Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet()) {
                functions.put(function.getKey(), (IRFuncDecl) Lift(function.getValue()));
            }
            return new IRCompUnit(((IRCompUnit) input).name(), functions,  (IRCompUnit) input);
        }
        return input;
    }

    /**
     * DO constant folding for all kinds of IRNodes
     * 
     * @param input
     * @return folded IR nodes
     */
    static IRNode Folding(IRNode input) throws IRNodeNotMatchException {
        try {
            if (input instanceof IRExpr) {
                return FoldingExpr((IRExpr) input);
            } else if (input instanceof IRStmt) {
                return FoldingStmt((IRStmt) input);
            } else if (input instanceof IRFuncDecl) {
                return new IRFuncDecl(((IRFuncDecl) input).name(), FoldingStmt(((IRFuncDecl) input).body()));
            } else if (input instanceof IRCompUnit) {
                Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
                for (Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet()) {
                    functions.put(function.getKey(), (IRFuncDecl) Folding(function.getValue()));
                }
                return new IRCompUnit(((IRCompUnit) input).name(), functions,  (IRCompUnit) input);
            }
            throw new IRNodeNotMatchException(input);
        } catch (IRNodeNotMatchException e) {
            throw e;
        }
    }

    /**
     * canonicalize all expressions
     * 
     * @param input
     * @return
     * @throws IRNodeNotMatchException
     */
    static IRESeq CanonicalizeExpr(IRExpr input) throws IRNodeNotMatchException {
        if (input instanceof IRConst || input instanceof IRTemp || input instanceof IRName) {
            return new IRESeq(null, input);
        } else if (input instanceof IRBinOp) {
            IRExpr left = ((IRBinOp) input).left();
            IRExpr right = ((IRBinOp) input).right();
            IRESeq es1 = CanonicalizeExpr(left);
            IRESeq es2 = CanonicalizeExpr(right);
            IRStmt s1 = es1.stmt();
            IRExpr e1 = es1.expr();
            IRStmt s2 = es2.stmt();
            IRExpr e2 = es2.expr();

            if ((right instanceof IRConst || right instanceof IRTemp) &&
                    (left instanceof IRConst || left instanceof IRTemp)) {
                // Both left and right are not recursive
                return new IRESeq(IRSeqNoEmpty(s1, s2), new IRBinOp(((IRBinOp) input).opType(), e1, e2));
            } else if (right instanceof IRConst || right instanceof IRTemp) {
                // Only right is not recursive
                IRTemp t1 = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                return new IRESeq(new IRSeq(s1, new IRMove(t1, e1), s2),
                        new IRBinOp(((IRBinOp) input).opType(), t1, e2));
            } else if (left instanceof IRConst || left instanceof IRTemp) {
                // Only left is not recursive
                IRTemp t2 = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                return new IRESeq(new IRSeq(s1, s2, new IRMove(t2, e2)),
                        new IRBinOp(((IRBinOp) input).opType(), e1, t2));
            } else  {
                // Both left and right are not recursive
                IRTemp t1 = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                IRTemp t2 = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                return new IRESeq(new IRSeq(s1, new IRMove(t1, e1), s2, new IRMove(t2, e2)),
                        new IRBinOp(((IRBinOp) input).opType(), t1, t2));
            }
        } else if (input instanceof IRMem) {
            IRESeq es = CanonicalizeExpr(((IRMem) input).expr());
            IRStmt s = es.stmt();
            IRExpr e = es.expr();
            return new IRESeq(s, new IRMem(e));
        } else if (input instanceof IRCall) {
            IRESeq call = CanonicalizeIRCall((IRCall) input);
            IRTemp t = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
            ((IRSeq) call.stmt()).stmts().add(new IRMove(t, call.expr()));
            return new IRESeq(call.stmt(), t);
        } else if (input instanceof IRESeq) {
            IRStmt s1 = CanonicalizeStmt(((IRESeq) input).stmt());
            IRESeq es = CanonicalizeExpr(((IRESeq) input).expr());
            IRStmt s2 = es.stmt();
            IRExpr e = es.expr();
            return new IRESeq(IRSeqNoEmpty(s1, s2), e);
        } else {
            throw new IRNodeNotMatchException(input);
        }
    }

    private static IRESeq CanonicalizeIRCall(IRCall input) throws IRNodeNotMatchException {
        IRExpr target = ((IRCall) input).target();
        List<IRExpr> e = ((IRCall) input).args();

        List<IRStmt> rsl = new ArrayList<IRStmt>();
        List<IRExpr> tle = new ArrayList<IRExpr>();
        for (IRExpr e1 : e) {
            IRESeq ese1 = (IRESeq) Canonicalize(e1);
            rsl.add(ese1.stmt());
            e1 = ese1.expr();
            if (e1 instanceof IRTemp || e1 instanceof IRConst) {
                tle.add(e1);
            } else {
                IRTemp argTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                rsl.add(new IRMove(argTemp, e1));
                tle.add(argTemp);
            }
        }
        return new IRESeq(new IRSeq(rsl), new IRCall(target, tle));
    }

    /**
     * canonicalize statement
     * 
     * @param input
     * @return
     * @throws IRNodeNotMatchException
     */
    static IRSeq CanonicalizeStmt(IRStmt input) throws IRNodeNotMatchException {
        if (input instanceof IRSeq) {
            List<IRStmt> stmts = ((IRSeq) input).stmts();
            List<IRStmt> results = new ArrayList<IRStmt>();
            for (IRStmt stmt : stmts) {
                results.add(CanonicalizeStmt(stmt));
            }
            return new IRSeq(results);
        } else if (input instanceof IRMove) {
            // target maybe IRESeq, IRTemp, IRMem. If IRESeq, first canonicalize
            // it to get stmt and expr (which can only IRTemp or IRMem).
            IRExpr e1 = ((IRMove) input).target();
            IRExpr e2 = ((IRMove) input).source();
            IRESeq e1cano = CanonicalizeExpr(e1);
            IRStmt s1 = e1cano.stmt();
            e1 = e1cano.expr();

            // If source if IRCall (which will be allocated with a new temp),
            // remove that new temp since it's useless.
            // TODO However, since there are bugs in assembly implementation, don't use it
            // for now.
            IRESeq es2;
            if (e1 instanceof IRTemp && e2 instanceof IRCall)
                es2 = CanonicalizeIRCall((IRCall) e2);
            else
                es2 = CanonicalizeExpr(e2);
            IRSeq s2p = (IRSeq) es2.stmt();
            IRExpr e2p = es2.expr();
            if (e1 instanceof IRTemp) {
                return (IRSeqNoEmpty(s1, s2p, new IRMove(e1, e2p)));
            } else if (e1 instanceof IRMem) {
                e1 = ((IRMem) e1).expr();
                IRESeq es1 = CanonicalizeExpr(e1);
                IRStmt s1p = es1.stmt();
                IRExpr e1p = es1.expr();
                IRTemp t = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                return (IRSeqNoEmpty(s1, s1p, new IRMove(t, e1p), s2p, new IRMove(new IRMem(t), e2p)));
            } else {
                return IRSeqNoEmpty(input);
            }
        } else if (input instanceof IRExp) {
            if (((IRExp) input).expr() instanceof IRCall) {
                IRESeq es = CanonicalizeIRCall((IRCall) ((IRExp) input).expr());
                return new IRSeq(es.stmt(), new IRExp(es.expr()));
            }
            else {
                IRESeq es = CanonicalizeExpr(((IRExp) input).expr());
                return new IRSeq(es.stmt());
            }
        } else if (input instanceof IRReturn) {
            List<IRExpr> e = ((IRReturn) input).rets();
            List<IRStmt> rsl = new ArrayList<IRStmt>();
            List<IRExpr> tle = new ArrayList<IRExpr>();
            for (IRExpr e1 : e) {
                IRESeq ese1 = (IRESeq) Canonicalize(e1);
                rsl.add(ese1.stmt());
                e1 = ese1.expr();
                if (e1 instanceof IRTemp || e1 instanceof IRConst) {
                    tle.add(e1);
                } else {
                    IRTemp argTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                    rsl.add(new IRMove(argTemp, e1));
                    tle.add(argTemp);
                }
            }
            rsl.add(new IRReturn(tle));
            IRStmt s = new IRSeq(rsl);
            return new IRSeq(s);
        } else if (input instanceof IRCJump) {
            IRESeq es = CanonicalizeExpr(((IRCJump) input).cond());
            IRStmt s = es.stmt();
            IRExpr e = es.expr();
            String l1 = ((IRCJump) input).trueLabel();
            String l2 = ((IRCJump) input).falseLabel();
            if (e instanceof IRTemp || e instanceof IRConst) {
                return IRSeqNoEmpty(s, new IRCJump(e, l1, l2));
            } else {
                IRTemp temp = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
                return IRSeqNoEmpty(s, new IRMove(temp, e), new IRCJump(temp, l1, l2));
            }
            
        } else if (input instanceof IRJump) {
            IRESeq es = CanonicalizeExpr(((IRJump) input).target());
            IRStmt s = es.stmt();
            IRExpr e = es.expr();
            // IRStmt s = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).stmt();
            // IRExpr e = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).expr();
            return IRSeqNoEmpty(s, new IRJump(e));
        } else {
            return new IRSeq(input);
        }
    }

    /**
     * folding constants in expressions
     * 
     * @param input
     * @return
     */
    static IRExpr FoldingExpr(IRExpr input) throws IRNodeNotMatchException {
        if (input instanceof IRConst) {
            return input;
        } else if (input instanceof IRTemp) {
            return input;
        } else if (input instanceof IRBinOp) {
            return FoldingBinOp((IRBinOp) input);
        } else if (input instanceof IRMem) {
            return new IRMem(FoldingExpr(((IRMem) input).expr()));
        } else if (input instanceof IRCall) {
            List<IRExpr> args = new ArrayList<IRExpr>();
            for (IRExpr arg : ((IRCall) input).args()) {
                args.add(FoldingExpr(arg));
            }
            return new IRCall(((IRCall) input).target(), args);
        } else if (input instanceof IRName) {
            return input;
        } else if (input instanceof IRESeq) {
            return new IRESeq(FoldingStmt(((IRESeq) input).stmt()), FoldingExpr(((IRESeq) input).expr()));
        } else {
            return input;
        }
    }

    /**
     * Folding stmt (stmt can contain child nodes with BinOp)
     * 
     * @param input
     * @return
     */
    static IRStmt FoldingStmt(IRStmt input) throws IRNodeNotMatchException {
        if (input instanceof IRLabel) {
            return input;
        } else if (input instanceof IRSeq) {
            List<IRStmt> stmts = new ArrayList<IRStmt>();
            for (IRStmt stmt : ((IRSeq) input).stmts()) {
                stmts.add(FoldingStmt(stmt));
            }
            return new IRSeq(stmts);
        } else if (input instanceof IRMove) {
            return new IRMove(FoldingExpr(((IRMove) input).target()), FoldingExpr(((IRMove) input).source()));
        } else if (input instanceof IRExp) {
            return new IRExp(FoldingExpr(((IRExp) input).expr()));
        } else if (input instanceof IRReturn) {
            List<IRExpr> exprs = new ArrayList<IRExpr>();
            for (IRExpr expr : ((IRReturn) input).rets()) {
                exprs.add(FoldingExpr(expr));
            }
            return new IRReturn(exprs);
        } else if (input instanceof IRCJump) {
            return new IRCJump(FoldingExpr(((IRCJump) input).cond()), ((IRCJump) input).trueLabel(),
                    ((IRCJump) input).falseLabel());
        } else if (input instanceof IRJump) {
            return new IRJump(FoldingExpr(((IRJump) input).target()));
        } else {
            return input;
        }
    }

    /**
     * folding the IRBinOp
     * 
     * @param input
     * @return the folded IRNode
     */
    static IRExpr FoldingBinOp(IRBinOp input) throws IRNodeNotMatchException {
        IRExpr lexp = ((IRBinOp) input).left();
        IRExpr rexp = ((IRBinOp) input).right();

        if (!(lexp instanceof IRConst)) {
            lexp = FoldingExpr(lexp);
        }
        if (!(rexp instanceof IRConst)) {
            rexp = FoldingExpr(rexp);
        }

        // Both left and right are constants
        if ((lexp instanceof IRConst && rexp instanceof IRConst)) {
            long l = lexp.constant();
            long r = rexp.constant();
            switch (((IRBinOp) input).opType()) {
            case ADD:
                return new IRConst(l + r);
            case SUB:
                return new IRConst(l - r);
            case MUL:
                return new IRConst(l * r);
            case HMUL:
                return new IRConst(BigInteger.valueOf(l).multiply(BigInteger.valueOf(r)).shiftRight(64).longValue());
            case DIV:
                if (r != 0) {
                    return new IRConst(l / r);
                } else {
                    break;
                }
            case MOD:
                if (r != 0) {
                    return new IRConst(l % r);
                } else {
                    break;
                }
            case AND:
                return new IRConst(l & r);
            case OR:
                return new IRConst(l | r);
            case XOR:
                return new IRConst(l ^ r);
            case LSHIFT:
                return new IRConst(l << r);
            case RSHIFT:
                return new IRConst(l >>> r);
            case ARSHIFT:
                return new IRConst(l >> r);
            case EQ:
                return new IRConst(l == r ? 1 : 0);
            case NEQ:
                return new IRConst(l != r ? 1 : 0);
            case LT:
                return new IRConst(l < r ? 1 : 0);
            case GT:
                return new IRConst(l > r ? 1 : 0);
            case LEQ:
                return new IRConst(l <= r ? 1 : 0);
            case GEQ:
                return new IRConst(l >= r ? 1 : 0);
            default:
                break;
            }
        }

        if (lexp instanceof IRConst && lexp.constant() == 0 || rexp instanceof IRConst && rexp.constant() == 0) {
            switch (((IRBinOp) input).opType()) {
            case ADD:
                return lexp instanceof IRConst ? rexp : lexp;
            case MUL:
            case HMUL:
                return new IRConst(0);
            case DIV:
            case MOD:
                if (lexp instanceof IRConst) {
                    return new IRConst(0);
                } else {
                    break;
                }
            case AND:
                return new IRConst(0);
            case OR:
                return lexp instanceof IRConst ? rexp : lexp;
            default:
                break;
            }
        }

        if (lexp instanceof IRConst && lexp.constant() == 1 || rexp instanceof IRConst && rexp.constant() == 1) {
            switch (((IRBinOp) input).opType()) {
            case MUL:
            case HMUL:
                return lexp instanceof IRConst ? rexp : lexp;
            case DIV:
            case MOD:
                if (lexp instanceof IRConst) {
                    break;
                } else {
                    return lexp;
                }
            case AND:
                return lexp instanceof IRConst ? rexp : lexp;
            case OR:
                return new IRConst(1);
            default:
                break;
            }
        }

        return new IRBinOp(input.opType(), lexp, rexp);
    }

    /**
     * Constuct IRSeq only when all the stmts are not null
     * 
     * @param stmts
     * @return IRSeq
     */
    static IRSeq IRSeqNoEmpty(IRStmt... stmts) {
        List<IRStmt> retStmts = new ArrayList<IRStmt>();
        for (IRStmt stmt : stmts) {
            if (stmt != null) {
                retStmts.add(stmt);
            }
        }
        return new IRSeq(retStmts);
    }

    /**
     * lift all stmts in an IR tree to the top-level list e.g., (SEQ stmt1 (SEQ
     * stmt2 (SEQ stmt3 stmt4) would become (SEQ stmt1 stmt2 stmt3 stmt4)
     * 
     * @param input
     * @return
     */
    static List<IRStmt> LiftSeq(IRStmt input) {
        if (input instanceof IRSeq) {
            List<IRStmt> stmts = ((IRSeq) input).stmts();
            List<IRStmt> results = new ArrayList<IRStmt>();
            for (IRStmt stmt : stmts) {
                if (stmt instanceof IRSeq) {
                    results.addAll(LiftSeq(stmt));
                } else {
                    if (stmt != null)
                        results.add(stmt);
                }
            }
            return results;
        } else {
            List<IRStmt> results = new ArrayList<IRStmt>();
            results.add(input);
            return results;
        }
    }
}
