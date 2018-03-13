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
import yh326.typecheck.TypecheckerWrapper;
import java.util.UUID;

public class IRWrapper {
	public Set<String> tempSet = new HashSet<String>();
	
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
            	    /*IRNode irNode = new IRSeq(new IRMove(new IRTemp("i"), new IRTemp("sb1")),
                        new IRMove(new IRTemp("j"), new IRTemp("sb2")),
                        new IRReturn(new IRTemp("i"),
                                new IRBinOp(OpType.MUL,
                                        new IRConst(2),
                                        new IRTemp("j")))); 
	            */
            	    		
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
	           writer.write(irNode.toString());
	            System.out.print(irNode.toString());
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
	
	// canonicalize IRNode (which can be IRExpr, IRStmt, IRFuncDecl, IRCompUnit)
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
		} catch (Exception e) {
			throw new IRNodeNotMatchException(input);
		}
	}
	
	// Canonicalize will turn all non-leaf node IRSeq or IRESeq, lift will lift all these nodes to the top
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
	
	// folding arbitrary stmt or expr
	static IRNode Folding(IRNode input) {
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
			return new IRCompUnit(((IRCompUnit) input).name(), ((IRCompUnit) input).functions());
		}
		return input;
	}
	
	
	// canonicalize all expressions
	static IRESeq CanonicalizeExpr(IRExpr input) throws IRNodeNotMatchException {
		if (input instanceof IRConst) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRTemp) {
			return new IRESeq(null, (IRExpr) input);
		} else if (input instanceof IRBinOp) {
			IRStmt s1 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).left())).stmt();
			IRExpr e1 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).left())).expr();
			IRStmt s2 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).right())).stmt();
			IRExpr e2 = ((IRESeq) CanonicalizeExpr(((IRBinOp) input).right())).expr();
			//return new IRESeq(IRSeqNoEmpty(s1, s2), new IRBinOp(((IRBinOp) input).opType(),e1, e2));
			IRTemp t1 = new IRTemp(UUID.randomUUID().toString().replaceAll("-", "")); //TODO pickaname
			return new IRESeq(new IRSeq(s1, new IRMove(t1, e1), s2), new IRBinOp(((IRBinOp) input).opType(),(IRExpr) t1, e2));
		} else if (input instanceof IRMem) {
			IRStmt s = (((IRESeq) CanonicalizeExpr(((IRMem) input).expr())).stmt());
			IRExpr e = ((IRESeq) CanonicalizeExpr(((IRMem) input).expr())).expr();
			return new IRESeq(s, new IRMem(e));
		} else if (input instanceof IRCall) {
			IRExpr target =((IRCall) input).target();
			List<IRExpr>  e=((IRCall) input).args();
			List<IRStmt> sl = new ArrayList<IRStmt>();
			List<IRExpr>  el = new ArrayList<IRExpr>();
			List<IRTemp> tl = new ArrayList<IRTemp>();
			int count = 0;
			String tempArrayName = UUID.randomUUID().toString().replaceAll("-", "");
			for (IRExpr e1: e) {
				sl.add(((IRESeq) CanonicalizeExpr(e1)).stmt());
				el.add(((IRESeq) CanonicalizeExpr(e1)).expr());
				tl.add(new IRTemp(tempArrayName+Integer.toString(count)));
				count++;
			}
	
			List<IRStmt> rsl = new ArrayList<IRStmt>();
			IRTemp t = new IRTemp(tempArrayName);
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
			throw new IRNodeNotMatchException(input);
			//return new IRESeq(null,input);
		}
	}
	
	// canonicalize statement
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
				/*if (((IRESeq) CanonicalizeExpr(e2)).expr() ==((IRESeq) CanonicalizeExpr(new IRESeq(((IRESeq) CanonicalizeExpr(target)).stmt(), e2))).expr()) {
					IRStmt s1p = ((IRESeq) CanonicalizeExpr(((IRMove) input).target())).stmt();
					IRExpr e1p = ((IRESeq) CanonicalizeExpr(((IRMove) input).target())).expr();
					IRStmt s2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).stmt();
					IRExpr e2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).expr();
					// only if e2 does not affect the location of e1
					return IRSeqNoEmpty(s1p, s2p,new IRMove(e1p, e2p));	
				} else {*/
					IRExpr e1 =((IRMem) ((IRMove) input).target()).expr();
					IRStmt s1p = ((IRESeq) CanonicalizeExpr(e1)).stmt();
					IRExpr e1p = ((IRESeq) CanonicalizeExpr(e1)).expr();
					IRStmt s2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).stmt();
					IRExpr e2p = ((IRESeq) CanonicalizeExpr(((IRMove) input).source())).expr();
					IRTemp t = new IRTemp(UUID.randomUUID().toString().replaceAll("-", "")); // sb must be fresh TODO		
					return IRSeqNoEmpty(s1p, new IRMove(t, e1p), s2p, new IRMove(new IRMem(t), e2p));
				//}
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
						sl.add(   CanonicalizeStmt(   ((IRESeq) CanonicalizeExpr(e1)).stmt()   )    );
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
			throw new IRNodeNotMatchException(input);
		}
	}
	
	// folding expr 
	static IRExpr FoldingExpr(IRExpr input) {
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
	
	// Folding stmt (stmt can contain child nodes with BinOp) 
	static IRStmt FoldingStmt(IRStmt input) {
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
	
	// only folding IRBinOp
	// returns the folded IRNode 
	static IRExpr FoldingBinOp(IRBinOp input) {
			IRExpr lexp = ((IRBinOp) input).left();
			IRExpr rexp = ((IRBinOp) input).right();
			
			if ((lexp.isConstant()) && (rexp.isConstant())) {
				long l = ((IRConst) lexp).value();
				long r = ((IRConst) rexp).value();
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
						}
					case MOD:
						if ( r != 0 ) {
							return new IRConst(l % r);
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
				return input;
			} else if (lexp.isConstant()) {
				return new IRBinOp(((IRBinOp) input).opType(), lexp, (IRExpr) Folding(rexp) );
			} else if (rexp.isConstant()) {
				return new IRBinOp(((IRBinOp) input).opType(), (IRExpr) Folding(lexp), rexp );
			} else {
				return new IRBinOp(((IRBinOp) input).opType(), (IRExpr) Folding(lexp), (IRExpr) Folding(rexp) );
			}
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
	
    // lift all stmts in an IR tree to the top level list
    static List<IRStmt> LiftSeq(IRStmt input) {
    	  if ( input instanceof IRSeq) {
		List<IRStmt> stmts = ((IRSeq) input).stmts();
 		List<IRStmt> results = new ArrayList<IRStmt>();
 		for (IRStmt stmt : stmts) {
 			if ( stmt instanceof IRSeq) {   
 				results.addAll(LiftSeq(stmt));
 			} else {
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
