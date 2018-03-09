package edu.cornell.cs.cs4120.xic.ir;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.visit.CanonicalizeIRVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.CheckCanonicalIRVisitor;

/**
 * An intermediate representation for expressions
 */
public abstract class IRExpr_c extends IRNode_c implements IRExpr {

    @Override
    public CheckCanonicalIRVisitor checkCanonicalEnter(
            CheckCanonicalIRVisitor v) {
        return v.enterExpr();
    }

    @Override
    public boolean isCanonical(CheckCanonicalIRVisitor v) {
        return v.inExpr() || !v.inExp();
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public long constant() {
        throw new UnsupportedOperationException();
    }
        
    @Override
    public IRNode Canonicalize(CanonicalizeIRVisitor v) {
    		if (this instanceof IRConst) {
			return new IRESeq(null, this);
			//return this;

		} else if (this instanceof IRTemp) {
			return new IRESeq(null, this);
			//return this;
		} else if (this instanceof IRBinOp) {
			// need to change later
			IRStmt s1 =((IRESeq) ((IRBinOp) this).left().Canonicalize(v)).stmt();
			IRExpr e1 =((IRESeq) ((IRBinOp) this).left().Canonicalize(v)).expr();
			IRStmt s2 =	((IRESeq) ((IRBinOp) this).right().Canonicalize(v)).stmt();
			IRExpr e2 = ((IRESeq) ((IRBinOp) this).right().Canonicalize(v)).expr();
			return new IRESeq(new IRSeq(s1,s2), new IRBinOp(((IRBinOp) this).opType(),e1, e2));
		} else if (this instanceof IRMem) {
			IRStmt s = ((IRESeq) ((IRMem) this).expr().Canonicalize(v)).stmt();
			IRExpr e = ((IRESeq) ((IRMem) this).expr().Canonicalize(v)).expr();
			return new IRESeq(s, new IRMem(e));
		} else if (this instanceof IRCall) {
			IRExpr target =((IRCall) this).target();
			List<IRExpr>  e=((IRCall) this).args();

			List<IRStmt> sl = new ArrayList<IRStmt>();
			List<IRExpr>  el = new ArrayList<IRExpr>();
			List<IRTemp> tl = new ArrayList<IRTemp>();
			int count = 0;
			for (IRExpr e1: e) {
				sl.add(((IRESeq) e1.Canonicalize(v)).stmt());
				el.add(((IRESeq) e1.Canonicalize(v)).expr());
				tl.add(new IRTemp("t"+Integer.toString(count)));
				count++;
			}
			
			List<IRStmt> rsl = new ArrayList<IRStmt>();
			IRTemp t = new IRTemp("t");
			count = 0;
			for (IRExpr e1 : e ) {
				rsl.add(sl.get(count));
				rsl.add(new IRMove(tl.get(count), el.get(count)));				
			}
			rsl.add(new IRMove(t, new IRCall(target, el)));
			IRStmt s = new IRSeq(rsl);
 			return new IRESeq(s, t );
		} else if (this instanceof IRName) {
			return this;
		} else if (this instanceof IRESeq) {
			IRStmt s1= ((IRESeq) this).stmt();
			IRStmt s2 =((IRESeq) ((IRESeq) this).expr().Canonicalize(v)).stmt();
			IRExpr e =((IRESeq) ((IRESeq) this).expr().Canonicalize(v)).expr();
			return new IRESeq(new IRSeq(s1,s2), e);
		} else {
			return this;
		}
    }
}
