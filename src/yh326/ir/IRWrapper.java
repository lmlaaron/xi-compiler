package yh326.ir;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRExpr_c;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRNodeFactory_c;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.parse.IRLexer;
import edu.cornell.cs.cs4120.xic.ir.parse.IRParser;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import yh326.ast.node.Node;
import yh326.exception.XiException;
import yh326.typecheck.TypecheckerWrapper;
import yh326.util.NumberGetter;
/**
 * Wrapper for IR generation, canonicalization, and constant folding
 * @author lmlaaron
 *
 */
public class IRWrapper {
	public static void IRLowering(String realInputFile, String realOutputDir, 
			String fileName, String libPath, boolean optimization) {
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".ir";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        try {
			FileWriter writer = new FileWriter(realOutputFile);
            try {
            	     FileReader r = new FileReader(realInputFile);
            		IRParser parser = new IRParser(new  IRLexer(r), new IRNodeFactory_c());
            	    IRNode irNode = null;
            	    
            	    try  {
            	    		irNode = parser.parse().value();
            	    } catch ( RuntimeException e) {
            	    		throw e;
            	    }
	            writer.write(irNode.toString());
	            System.out.print(irNode.toString());
	            
	            // IR lowering
	            System.out.print("Canonicalized node:\n");
	            IRNode canonicalizedIrNode = Canonicalize(irNode);
	            //System.out.print(canonicalizedIrNode.toString()); do not print canonicalizedIrNode, it cannot print empty IRSeq
	            
	            // IR lift	          
	            IRNode liftedIrNode = Lift(canonicalizedIrNode);
	            System.out.print("Lifted node:\n");
	            System.out.print(liftedIrNode.toString());
	            
	            // IR folding
	            IRNode foldedIrNode = Folding(liftedIrNode);
	            System.out.print("Folded node:\n");
	            System.out.print(foldedIrNode.toString());
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
	
    /**
     * Generate the IR file
     */
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
	            System.out.println(irNode.toString());
	            irNode = Canonicalize(irNode);
	            irNode = Lift(irNode);
	            if (optimization) {
	            	irNode = Folding(irNode);
	            }
            	System.out.println("Lowered folded IrNode:");
            	System.out.println(irNode.toString());
            	writer.write(irNode.toString());
	        } catch (XiException e) {
	        		e.print(fileName);
	        		writer.write(e.getMessage() + "\n");
	        } catch (Exception e) {
                e.printStackTrace();
            }
            writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return;
	}
	
	/**
	 * canonicalize IRNode (which can be IRExpr, IRStmt, IRFuncDecl, IRCompUnit),
	 * @return: IRNode tree with all non-leaf node as SEQ or ESEQ
	 */
	static IRNode Canonicalize(IRNode input) throws IRNodeNotMatchException {
		try {
		  if (input instanceof IRExpr) {
		  	return CanonicalizeExpr((IRExpr) input);
		  } else if ( input instanceof IRStmt) {
			return CanonicalizeStmt((IRStmt) input);
	      } else if ( input instanceof IRFuncDecl ) {
	    	    return new IRFuncDecl(((IRFuncDecl) input).name(), CanonicalizeStmt(((IRFuncDecl) input).body()));
		  } else if ( input instanceof IRCompUnit) {
			Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
			for ( Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet() ) {
				functions.put(function.getKey(), (IRFuncDecl) Canonicalize(function.getValue()));
			}
			return new IRCompUnit(((IRCompUnit) input).name(), functions);
		  } else {
			  throw new IRNodeNotMatchException(input);
		  }
		} catch (IRNodeNotMatchException e) {
			throw new IRNodeNotMatchException(input);
		}
	}
	
	/**
	 * Canonicalize will turn all non-leaf node IRSeq or IRESeq, lift will lift all these nodes to the top
	 */
	static IRNode Lift(IRNode input) {
		if (input instanceof IRSeq) {
			return new IRSeq(LiftSeq((IRStmt) input));			
		} else if ( input instanceof IRESeq) {
			return input;
		} else if ( input instanceof IRStmt ) {
			return input;
		} else if ( input instanceof IRExpr) {
			return input;
		} else if (input instanceof IRFuncDecl) {
			return new IRFuncDecl(((IRFuncDecl) input).name(), (IRStmt) Lift(((IRFuncDecl) input).body()));
		} else if ( input instanceof IRCompUnit) {
			Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
			for ( Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet() ) {
				functions.put(function.getKey(), (IRFuncDecl) Lift(function.getValue()));
			}
			return new IRCompUnit(((IRCompUnit) input).name(), functions);
		}
		return input;
	}
	
	/**
	 * DO constant folding for all kinds of IRNodes
	 * @param input
	 * @return folded IR nodes
	 */
	static IRNode Folding(IRNode input) throws IRNodeNotMatchException {
		try {
			if (input instanceof IRExpr) {
				return FoldingExpr((IRExpr) input);
			} else if (input instanceof IRStmt) {
				return FoldingStmt((IRStmt) input);
			} else if ( input instanceof IRFuncDecl ) {
				return new IRFuncDecl(((IRFuncDecl) input).name(), FoldingStmt(((IRFuncDecl) input).body()));
			} else if ( input instanceof IRCompUnit) {
				Map<String, IRFuncDecl> functions = new LinkedHashMap<>();
				for ( Map.Entry<String, IRFuncDecl> function : ((IRCompUnit) input).functions().entrySet() ) {
				functions.put(function.getKey(), (IRFuncDecl) Folding(function.getValue()));
				}
			return new IRCompUnit(((IRCompUnit) input).name(), functions);
			}
			throw new IRNodeNotMatchException(input);
		} catch (IRNodeNotMatchException e ) {
			throw e;
		}
	}
	
	/**
	 * canonicalize all expressions
	 * @param input
	 * @return
	 * @throws IRNodeNotMatchException
	 */
	static IRESeq CanonicalizeExpr(IRExpr input) throws IRNodeNotMatchException {
		if (input instanceof IRConst) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRTemp) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRBinOp) {
			IRESeq es1 = CanonicalizeExpr(((IRBinOp) input).left());
			IRESeq es2 = CanonicalizeExpr(((IRBinOp) input).right());
			IRStmt s1 = es1.stmt();
			IRExpr e1 = es1.expr();
			IRStmt s2 = es2.stmt();
			IRExpr e2 = es2.expr();
			
			// only if right operand has no side effect or left operand is immutable
			if ((((IRBinOp) input).right()  instanceof IRName) || 
					(( ( IRBinOp) input).right() instanceof IRConst || 
							((IRBinOp) input).right() instanceof IRTemp ||
							((IRBinOp) input).left() instanceof IRName ||
							((IRBinOp) input).left() instanceof IRConst)) {
						return new IRESeq(IRSeqNoEmpty(s1, s2), new IRBinOp(((IRBinOp) input).opType(),e1, e2));
					} else {
						IRTemp t1 = new IRTemp("_temp_" + NumberGetter.uniqueNumber());
						return new IRESeq(new IRSeq(s1, new IRMove(t1, e1), s2), new IRBinOp(((IRBinOp) input).opType(),(IRExpr) t1, e2));
					}
		} else if (input instanceof IRMem) {
			IRESeq es =CanonicalizeExpr(((IRMem) input).expr());
			IRStmt s = es.stmt();
			IRExpr e =  es.expr();
			return new IRESeq(s, new IRMem(e));
		} else if (input instanceof IRCall) {
			IRExpr target =((IRCall) input).target();
			List<IRExpr>  e=((IRCall) input).args();
			List<IRStmt> sl = new ArrayList<IRStmt>();
			List<IRExpr>  el = new ArrayList<IRExpr>();
			List<IRTemp> tl = new ArrayList<IRTemp>();
			int count = 0;
			String tempArrayName = "_temp_" + NumberGetter.uniqueNumber();
			for (IRExpr e1: e) {
				IRESeq ese1 = (IRESeq) Canonicalize(e1);
				sl.add(ese1.stmt());
				el.add(ese1.expr());
				tl.add(new IRTemp(tempArrayName+"_"+Integer.toString(count)));
				count++;
			}
	
			List<IRStmt> rsl = new ArrayList<IRStmt>();
			IRTemp t = new IRTemp(tempArrayName);
			count = 0;
			List<IRExpr> tle = new ArrayList<IRExpr>();
			for (IRTemp stl: tl) {
				tle.add(stl);
			}
			for (IRExpr e1 : e ) {
				if ( e1 instanceof IRTemp || e1 instanceof IRConst || e1 instanceof IRName) {
					tle.set(count, e1);
				} else {
				  rsl.add(sl.get(count));
				  rsl.add(new IRMove(tl.get(count), el.get(count)));				
				}
				count++;
			}

			rsl.add(new IRMove(t, new IRCall(target, tle)));
			IRStmt s = new IRSeq(rsl);
			return new IRESeq(s, t );
		} else if (input instanceof IRName) {
			return new IRESeq(null,input);
		} else if (input instanceof IRESeq) {
			IRStmt s1= CanonicalizeStmt(((IRESeq) input).stmt());
			IRESeq es = CanonicalizeExpr(((IRESeq) input).expr());
			IRStmt s2 =es.stmt();
			IRExpr e =es.expr();
			return new IRESeq(IRSeqNoEmpty(s1,s2), e);
		} else {
			throw new IRNodeNotMatchException(input);
			//return new IRESeq(null,input);
		}
	}
	/**
	 * canonicalize statement
	 * @param input
	 * @return
	 * @throws IRNodeNotMatchException
	 */
	static IRSeq CanonicalizeStmt(IRStmt input ) throws IRNodeNotMatchException {
		if (input instanceof IRLabel) {
			return new IRSeq(input);
		} else if (input instanceof IRSeq) {
			List<IRStmt> stmts = ((IRSeq) input).stmts();
			if ( stmts.isEmpty()) {
				return ((IRSeq) input);
			}
    	 		List<IRStmt> results = new ArrayList<IRStmt>();
            for (IRStmt stmt : stmts) {
                IRStmt newStmt = (IRStmt) CanonicalizeStmt(stmt);
                if ( newStmt != null ) {
                		results.add(newStmt);
                }
            }
            return new IRSeq(results);
		} else if (input instanceof IRMove) {
			//System.out.println(input.toString());
			IRExpr target = ((IRMove) input).target();
			IRExpr e2 = ((IRMove) input).source();
			if (target instanceof IRTemp || e2 instanceof IRTemp || e2 instanceof IRName || e2 instanceof IRConst) {
				IRExpr e1 = target;
				IRESeq es1 = (IRESeq) CanonicalizeExpr(e1);
				IRESeq es2 = (IRESeq) CanonicalizeExpr(e2);
				IRStmt s1p = es1.stmt();
				IRExpr e1p = es1.expr();
				IRStmt s2p = es2.stmt();
				IRExpr e2p = es2.expr();
				return (IRSeqNoEmpty(s1p,s2p, new IRMove(e1p, e2p)));
			} else if (target instanceof IRMem) {
			//} else {
				//System.out.println(input.toString());			
				   IRExpr e1 =((IRMem) ((IRMove) input).target()).expr();
					IRESeq eseq_e1 = CanonicalizeExpr(e1);
					IRESeq eseq_e2 = CanonicalizeExpr(((IRMove) input).source());
					IRStmt s1p = eseq_e1.stmt();
					IRExpr e1p = eseq_e1.expr();
					IRStmt s2p = eseq_e2.stmt();
					IRExpr e2p = eseq_e2.expr();
					IRTemp t = new IRTemp("_temp_" + NumberGetter.uniqueNumber()); 
					return (IRSeqNoEmpty(s1p, new IRMove(t, e1p), s2p, new IRMove(new IRMem(t), e2p)));
			} else {
				return IRSeqNoEmpty(input);
			}
		} else if (input instanceof IRExp) {
			    IRESeq es = CanonicalizeExpr(((IRExp) input).expr());
				IRStmt s = es.stmt();
				IRExpr e = es.expr();
				return new IRSeq(s);
		} else if (input instanceof IRReturn) {
			List<IRExpr>  e= ((IRReturn) input).rets();
			List<IRStmt> sl = new ArrayList<IRStmt>();
			List<IRExpr>  el = new ArrayList<IRExpr>();
			List<IRTemp> tl = new ArrayList<IRTemp>();
			int count = 0;
			String tempArrayName = "_temp_" + NumberGetter.uniqueNumber();
			for (IRExpr e1: e) {
				IRESeq ese1 = (IRESeq) CanonicalizeExpr(e1);
				sl.add(ese1.stmt());
				el.add(ese1.expr());
				tl.add(new IRTemp(tempArrayName+"_"+Integer.toString(count)));
				count++;
			}
	
			List<IRStmt> rsl = new ArrayList<IRStmt>();
			count = 0;
			List<IRExpr> tle = new ArrayList<IRExpr>();
			for (IRTemp stl: tl) {
				tle.add(stl);
			}
			for (IRExpr e1 : e ) {
				if ( e1 instanceof IRTemp || e1 instanceof IRConst || e1 instanceof IRName) {
					tle.set(count, e1);
				} else {
				  rsl.add(sl.get(count));
				  rsl.add(new IRMove(tl.get(count), el.get(count)));				
				}
				count++;
			}

			rsl.add(new IRReturn(tle));
			IRStmt s = new IRSeq(rsl);
			return new IRSeq(s);
			
			/*
				List<IRExpr>  e = ((IRReturn) input).rets();
				List<IRStmt> sl = new ArrayList<IRStmt>();
				List<IRExpr>  el = new ArrayList<IRExpr>();
				for (IRExpr e1: e) {
					IRESeq es = (IRESeq) Canonicalize(e1);
					if (es.stmt() != null) {
						sl.add(   CanonicalizeStmt(   es.stmt()   )    );
					}
					el.add(es.expr());
				}
				sl.add(new IRReturn(el));
				return new IRSeq(sl);
				*/
		} else if (input instanceof IRCJump) {
			IRESeq es = CanonicalizeExpr(((IRCJump) input).cond());
			IRStmt s = es.stmt();
			IRExpr e = es.expr();
			String l1 = ((IRCJump) input).trueLabel();
			String l2 = ((IRCJump) input).falseLabel();
			return IRSeqNoEmpty(s, new IRCJump(e,l1,l2));
		} else if (input instanceof IRJump) {
			IRESeq es = CanonicalizeExpr(((IRJump) input).target());
			IRStmt s = es.stmt();
			IRExpr e = es.expr();
			//IRStmt s = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).stmt();
			//IRExpr e = ((IRESeq) CanonicalizeExpr(((IRJump) input).target())).expr();
			return IRSeqNoEmpty(s, new IRJump(e));
		} else {
			return new IRSeq(input);
			//throw new IRNodeNotMatchException(new IRSeq(seq));
		}
	}
	/**
	 * folding constants in expressions
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
			for ( IRExpr arg : ((IRCall) input).args()) {
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
	 * @param input
	 * @return
	 */
	static IRStmt FoldingStmt(IRStmt input) throws IRNodeNotMatchException {
		if (input instanceof IRLabel) {
			return input;
		} else if (input instanceof IRSeq) {
			List<IRStmt> stmts = new ArrayList<IRStmt>();
			for (IRStmt stmt: ((IRSeq) input).stmts()) {
				stmts.add(FoldingStmt(stmt));
			}
			return new IRSeq(stmts);
		} else if (input instanceof IRMove) {
			return new IRMove(FoldingExpr(((IRMove) input).target()), FoldingExpr(((IRMove) input).source()));
		} else if (input instanceof IRExp) {
			return new IRExp(FoldingExpr(((IRExp) input).expr()));
		} else if (input instanceof IRReturn) {
			List<IRExpr> exprs = new ArrayList<IRExpr>();
			for (IRExpr expr: ((IRReturn) input).rets()) {
				exprs.add(FoldingExpr(expr));
			}
			return new IRReturn(exprs);
		} else if (input instanceof IRCJump) {
			return new IRCJump(FoldingExpr(((IRCJump) input).cond()),((IRCJump) input).trueLabel(), ((IRCJump) input).falseLabel());
		} else if (input instanceof IRJump) {
			return new IRJump(FoldingExpr(((IRJump) input).target()));
		} else {
			return input;
		}
	}
	
	/**
	 * folding the IRBinOp
	 * @param input
	 * @return the folded IRNode
	 */
	static IRExpr FoldingBinOp(IRBinOp input) throws IRNodeNotMatchException {
			IRExpr lexp = ((IRBinOp) input).left();
			IRExpr rexp = ((IRBinOp) input).right();
			
			if ((lexp instanceof IRConst && rexp instanceof IRConst)) {
				long l=lexp.constant();
				long r = ((IRConst) rexp).constant();
				switch (((IRBinOp) input).opType()) {
					case ADD:
						return new IRConst(l+r);
					case SUB:
						return new IRConst(l-r);
					case MUL:
						return new IRConst(l*r);
					case HMUL:
						return new IRConst(
								BigInteger.valueOf(l)
                                .multiply(BigInteger.valueOf(r))
                                .shiftRight(64)
                                .longValue());
					case DIV:
						if ( r != 0 ) {
							return new IRConst(l/r);
						} else {
							return input;
						}
					case MOD:
						if ( r != 0 ) {
							return new IRConst(l % r);
						} else {
							return input;
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
						return new IRConst(l >>>  r);
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
						return new IRConst(l <= r ? 1: 0);
					case GEQ:
						return new IRConst(l >= r ? 1: 0);
				}
			}

			if (lexp instanceof IRConst) {
			} else if (lexp instanceof IRBinOp) {
				lexp = FoldingBinOp((IRBinOp) lexp); 
			} 
			if (rexp instanceof IRConst) {
			} else if (rexp instanceof IRBinOp ) {
				rexp = FoldingBinOp((IRBinOp) rexp);
			}
			if ( lexp instanceof IRConst && rexp instanceof IRConst) {
				return FoldingBinOp(new IRBinOp(input.opType(), lexp, rexp));				
			} else {
				return new IRBinOp(input.opType(),lexp,rexp);
			}
	}
	
	/**
	 * Constuct IRSeq only when all the stmts are not null
	 * @param stmts
	 * @return IRSeq
	 */
    static IRSeq IRSeqNoEmpty(IRStmt... stmts) {
        List<IRStmt> retStmts = new ArrayList<IRStmt>();
        for (IRStmt stmt: stmts) {
        		if (stmt != null) {
        			retStmts.add(stmt);
        		}
        }
        return new IRSeq(retStmts);
    }
	
    /**
     * lift all stmts in an IR tree to the top-level list
     * e.g.,
     * 	(SEQ stmt1 (SEQ stmt2 (SEQ stmt3 stmt4) would become
     * (SEQ stmt1 stmt2 stmt3 stmt4)
     * @param input
     * @return
     */
    static List<IRStmt> LiftSeq(IRStmt input) {
    	  if ( input instanceof IRSeq) {
		List<IRStmt> stmts = ((IRSeq) input).stmts();
 		List<IRStmt> results = new ArrayList<IRStmt>();
 		for (IRStmt stmt : stmts) {
 			if ( stmt instanceof IRSeq) {   
 				results.addAll(LiftSeq(stmt));
 			} else {
 				if (stmt != null )
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

	
	// Design not finished
	public static void IRRun() {
		
	}
}
