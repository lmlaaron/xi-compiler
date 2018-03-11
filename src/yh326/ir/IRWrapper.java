package yh326.ir;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRExpr_c;
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
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import yh326.ast.node.Node;
import yh326.typecheck.TypecheckerWrapper;

public class IRWrapper {

	public static void IRGeneration(String realInputFile, String realOutputDir, 
			String fileName, String libPath, boolean optimization) {
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".ir";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        try {
			FileWriter writer = new FileWriter(realOutputFile);
            try {
                Node ast = TypecheckerWrapper.getTypechecked(realInputFile, libPath);
                ast.fileName = fileName.substring(0, fileName.lastIndexOf("."));
                // Design not finished
	            IRNode irNode = ast.translateProgram();
	            /*IRNode irNode = new IRSeq(new IRMove(new IRTemp("i"), new IRTemp("sb1")),
                        new IRMove(new IRTemp("j"), new IRTemp("sb2")),
                        new IRReturn(new IRTemp("i"),
                                new IRBinOp(OpType.MUL,
                                        new IRConst(2),
                                        new IRTemp("j")))); 
	            */
	            writer.write(irNode.toString());
	            System.out.print(irNode.toString());
	            /*
	            // IR lowering
	            System.out.print("Canonicalized node:\n");
	            IRNode canonicalizedIrNode = Canonicalize2(irNode);
	            System.out.print(canonicalizedIrNode.toString());
	            
	            // IR lift	          
	            IRNode liftedIrNode = Lift(canonicalizedIrNode);
	            System.out.print("Canonicalized node:\n");
	            System.out.print(liftedIrNode.toString());
				*/
            } catch (Exception e) {
                writer.write(e.getMessage() + "\n");
                throw e;
            }
            writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return;
	}
	
    static IRSeq IRSeqNoEmpty(IRStmt... stmts) {
        List<IRStmt> retStmts = new ArrayList<IRStmt>();
        for (IRStmt stmt: stmts) {
        		if (stmt != null) {
        			retStmts.add(stmt);
        		}
        }
        return new IRSeq(retStmts);
    }
	
	// Canonicalize will turn all non-leaf node IRSeq or IRESeq, lift will lift all these nodes to the top
	static IRNode Lift(IRNode input) {
		if (input instanceof IRSeq) {
			List<IRStmt> stmts = ((IRSeq) input).stmts();
	 		List<IRStmt> results = new ArrayList<IRStmt>();
	 		for (IRStmt stmt : stmts) {
	 			if ( stmt instanceof IRSeq) {   
	 				List<IRStmt> substmts = ((IRSeq) stmt).stmts();
	 				for ( IRStmt substmt: substmts) {
	 					if (substmt != null ) {
	 						results.add((IRStmt) Lift(substmt));
	 					}
	 				}
	 			} else {
	 				if (stmt != null ) {
	 					results.add((IRStmt) Lift(stmt));
	 				}
	 			}
	 		}
	 		return new IRSeq(results);
		} else if ( input instanceof IRESeq) {
			List<IRStmt> stmts = ((IRSeq) ((IRESeq) input).stmt()).stmts();
			IRExpr expr = ((IRESeq) input).expr();
	 		List<IRStmt> results = new ArrayList<IRStmt>();
	 		for (IRStmt stmt : stmts) {
	 			if ( stmt instanceof IRSeq) {   
	 				List<IRStmt> substmts = ((IRSeq) stmt).stmts();
	 				for ( IRStmt substmt: substmts) {
	 					if (substmt != null ) {
	 						results.add((IRStmt) Lift(substmt));
	 					}
	 				}
	 			} else {
	 				if (stmt != null ) {
	 					results.add((IRStmt) Lift(stmt));
	 				}
	 			}
	 		}
	 		return new IRESeq(new IRSeq(results), expr);
		}
		return input;
	}
	
	// canonicalize all expressions
	static IRESeq CanonicalizeExpr(IRExpr input) {
		if (input instanceof IRConst) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRTemp) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRBinOp) {
			IRStmt s1 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).left())).stmt();
			IRExpr e1 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).left())).expr();
			IRStmt s2 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).right())).stmt();
			IRExpr e2 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).right())).expr();
			return new IRESeq(IRSeqNoEmpty(s1, s2), new IRBinOp(((IRBinOp) input).opType(),e1, e2));
		} else if (input instanceof IRMem) {
			IRStmt s = (((IRESeq) CanonicalizeExpr(((IRESeq) input).expr())).stmt());
			IRExpr e = ((IRESeq) CanonicalizeExpr(((IRESeq) input).expr())).expr();
			return new IRESeq(s, new IRMem(e));
		} else if (input instanceof IRCall) {
			IRExpr target =((IRCall) input).target();
			List<IRExpr>  e=((IRCall) input).args();
			List<IRStmt> sl = new ArrayList<IRStmt>();
			List<IRExpr>  el = new ArrayList<IRExpr>();
			List<IRTemp> tl = new ArrayList<IRTemp>();
			int count = 0;
			for (IRExpr e1: e) {
				sl.add(((IRESeq) CanonicalizeExpr(e1)).stmt());
				el.add(((IRESeq) CanonicalizeExpr(e1)).expr());
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
		} else if (input instanceof IRName) {
			return new IRESeq(null,input);
		} else if (input instanceof IRESeq) {
			IRStmt s1= ((IRESeq) input).stmt();
			IRStmt s2 =((IRESeq) CanonicalizeExpr(((IRESeq) input).expr())).stmt();
			IRExpr e =((IRESeq) CanonicalizeExpr(((IRESeq) input).expr())).expr();
			return new IRESeq(IRSeqNoEmpty(s1,s2), e);
		} else {
			return new IRESeq(null,input);
		}
	}
	
	static IRSeq CanonicalizeStmt(IRStmt input ) {
		if (input instanceof IRLabel) {
			return new IRSeq(input);
		} else if (input instanceof IRSeq) {
			List<IRStmt> stmts = ((IRSeq) input).stmts();
			if ( stmts.isEmpty()) {
				return null;
			}
    	 		List<IRStmt> results = new ArrayList<IRStmt>();
            for (IRStmt stmt : stmts) {
                IRStmt newStmt = (IRStmt) CanonicalizeStmt(stmt);        
                results.add(newStmt);
            }
            return new IRSeq(results);
		} else if (input instanceof IRMove) {
			IRExpr target = ((IRMove) input).target();
			IRExpr e2 = ((IRMove) input).source();
			if (target instanceof IRTemp) {
				IRExpr e1 = ((IRTemp) target);
				IRStmt s1p = ((IRESeq) CanonicalizeExpr(e1)).stmt();
				IRExpr e1p = ((IRESeq) CanonicalizeExpr(e1)).expr();
				IRStmt s2p = ((IRESeq) CanonicalizeExpr(e2)).stmt();
				IRExpr e2p = ((IRESeq) CanonicalizeExpr(e2)).expr();
				return IRSeqNoEmpty(s1p,s2p, new IRMove(e1p, e2p));
			} else if (target instanceof IRMem) {
				if (((IRESeq) CanonicalizeExpr(e2)).expr() ==((IRESeq) CanonicalizeExpr(new IRESeq(((IRESeq) CanonicalizeExpr(target)).stmt(), e2))).expr()) {
					IRStmt s1p = ((IRESeq) CanonicalizeExpr(((IRMove) input).target())).stmt();
					IRExpr e1p = ((IRESeq) CanonicalizeExpr(((IRMove) input).target())).expr();
					IRStmt s2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).stmt();
					IRExpr e2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).expr();
					// only if e2 does not affect the location of e1
					return IRSeqNoEmpty(s1p, s2p,new IRMove(e1p, e2p));	
				} else {
					IRExpr e1 =((IRMem) ((IRMove) input).target()).expr();
					IRStmt s1p = ((IRESeq) CanonicalizeExpr(e1)).stmt();
					IRExpr e1p = ((IRESeq) CanonicalizeExpr(e1)).expr();
					IRStmt s2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).stmt();
					IRExpr e2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).expr();
					IRTemp t = new IRTemp("sb"); // sb must be fresh TODO		
					return IRSeqNoEmpty(s1p, new IRMove(t, e1p), s2p, new IRMove(t, e2p));
				}
			} else {
				return IRSeqNoEmpty(input);
			}
		} else if (input instanceof IRExp) {
				IRStmt s = ((IRESeq) CanonicalizeExpr(((IRExp) input).expr())).stmt();
				IRExpr e = ((IRESeq) CanonicalizeExpr(((IRExp) input).expr())).expr();
				return new IRSeq(s);
		} else if (input instanceof IRReturn) {
				List<IRExpr>  e = ((IRReturn) input).rets();
				List<IRStmt> sl = new ArrayList<IRStmt>();
				List<IRExpr>  el = new ArrayList<IRExpr>();
				for (IRExpr e1: e) {
					if (((IRESeq) CanonicalizeExpr(e1)).stmt() != null) {
						sl.add(CanonicalizeStmt(((IRESeq) CanonicalizeExpr(e1)).stmt()));
					}
					el.add(((IRESeq) CanonicalizeExpr(e1)).expr());
				}
				sl.add(new IRReturn(el));
				return new IRSeq(sl);
		} else if (input instanceof IRCJump) {
			IRStmt s = ((IRESeq) CanonicalizeExpr(((IRCJump) input).cond())).stmt();
			IRExpr e = ((IRESeq) CanonicalizeExpr(((IRCJump) input).cond())).expr();
			String l1 = ((IRCJump) input).trueLabel();
			String l2 = ((IRCJump) input).falseLabel();
			return IRSeqNoEmpty(s, new IRCJump(e,l1,l2));
		} else if (input instanceof IRJump) {
			IRStmt s = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).stmt();
			IRExpr e = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).expr();
			return IRSeqNoEmpty(s, new IRJump(e));
		} else {
			return IRSeqNoEmpty(input);
		}
	}
	
	static IRNode Canonicalize2(IRNode input) {
		if (input instanceof IRExpr) {
			return CanonicalizeExpr((IRExpr) input);
		} else if ( input instanceof IRStmt) {
			return CanonicalizeStmt((IRStmt) input);
		}
		return input;
	}
	// Design not finished
	public static void IRRun() {
		
	}
}
