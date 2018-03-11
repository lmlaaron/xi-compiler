package yh326.ir;

import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	            //IRNode irNode = ast.translateProgram();
	            IRNode irNode = new IRSeq(new IRMove(new IRTemp("i"), new IRTemp("sb1")),
                        new IRMove(new IRTemp("j"), new IRTemp("sb2")),
                        new IRReturn(new IRTemp("i"),
                                new IRBinOp(OpType.MUL,
                                        new IRConst(2),
                                        new IRTemp("j")))); 
	            
	            //IR lowering
	            writer.write(irNode.toString());
	            System.out.print(irNode.toString());
	            //System.out.print("Canonicalized node:\n");
	            //IRNode canonicalizedIrNode = Canonicalize(irNode);
	            //System.out.print(canonicalizedIrNode.toString());
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
	
	static IRNode Canonicalize(IRNode input) {
		if ( input instanceof IRExpr_c) {
			if (input instanceof IRConst) {
				return new IRESeq(null, (IRExpr) input);
			} else if (input instanceof IRTemp) {
				return new IRESeq(null, (IRExpr) input);
			} else if (input instanceof IRBinOp) {
				IRStmt s1 = ((IRESeq) Canonicalize(((IRBinOp) input).left())).stmt();
				IRExpr e1 = ((IRESeq) Canonicalize(((IRBinOp) input).left())).expr();
				IRStmt s2 = ((IRESeq) Canonicalize(((IRBinOp) input).right())).stmt();
				IRExpr e2 = ((IRESeq) Canonicalize(((IRBinOp) input).right())).expr();
				return new IRESeq(new IRSeq(s1, s2), new IRBinOp(((IRBinOp) input).opType(),e1, e2));
			} else if (input instanceof IRMem) {
				IRStmt s = (((IRESeq) Canonicalize(((IRESeq) input).expr())).stmt());
				IRExpr e = ((IRESeq) Canonicalize(((IRESeq) input).expr())).expr();
				return new IRESeq(s, new IRMem(e));
			} else if (input instanceof IRCall) {
				IRExpr target =((IRCall) input).target();
				List<IRExpr>  e=((IRCall) input).args();
				List<IRStmt> sl = new ArrayList<IRStmt>();
				List<IRExpr>  el = new ArrayList<IRExpr>();
				List<IRTemp> tl = new ArrayList<IRTemp>();
				int count = 0;
				for (IRExpr e1: e) {
					sl.add(((IRESeq) Canonicalize(e1)).stmt());
					el.add(((IRESeq) Canonicalize(e1)).expr());
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
				return input;
			} else if (input instanceof IRESeq) {
				IRStmt s1= ((IRESeq) input).stmt();
				IRStmt s2 =((IRESeq) Canonicalize(((IRESeq) input).expr())).stmt();
				IRExpr e =((IRESeq) Canonicalize(((IRESeq) input).expr())).expr();
				return new IRESeq(new IRSeq(s1,s2), e);
			} else {
				return input;
			}
			
			
		} else if (input instanceof IRStmt) {
			if (input instanceof IRLabel) {
				return input;
			} else if (input instanceof IRSeq) {
				List<IRStmt> stmts = ((IRSeq) input).stmts();
	    	 		List<IRStmt> results = new ArrayList<IRStmt>();
	            for (IRStmt stmt : stmts) {
	                IRStmt newStmt = (IRStmt) Canonicalize(stmt);        
	                results.add(newStmt);
	            }
	            return new IRSeq(results);
			} else if (input instanceof IRESeq) { 
				IRStmt stmt = ((IRESeq) input).stmt();
				IRExpr expr = ((IRESeq) input).expr();
				IRStmt s = ((IRESeq) Canonicalize(expr)).stmt();
				IRExpr e = ((IRESeq) Canonicalize(expr)).expr();
				return new IRESeq(new IRSeq(stmt,s), e);
			} else if (input instanceof IRMove) {
				IRExpr target = ((IRMove) input).target();
				IRExpr e2 = ((IRMove) input).source();
				if (target instanceof IRTemp) {
					IRExpr e1 = ((IRTemp) target);
					IRStmt s1p = ((IRESeq) Canonicalize(e1)).stmt();
					IRExpr e1p = ((IRESeq) Canonicalize(e1)).expr();
					IRStmt s2p = ((IRESeq) Canonicalize(e2)).stmt();
					IRExpr e2p = ((IRESeq) Canonicalize(e2)).expr();
					return new IRSeq(s1p,s2p, new IRMove(new IRMem(e1p), e2p));
				} else if (target instanceof IRMem) {
				/*	if (Canonicalize(e2.epxr()).expr() == Canonicalize(new IRESeq(target.stmt(),e2.expr()))) {
						IRStmt s1p = ((IRESeq) Canonicalize(((IRMove) input).target())).stmt();
						IRExpr e1p = ((IRESeq) Canonicalize(((IRMove) input).target())).expr();
						IRStmt s2p = ((IRESeq) Canonicalize(((IRMove) input).source())).stmt();
						IRExpr e2p = ((IRESeq) Canonicalize(((IRMove) input).source())).expr();
						// only if e2 does not affect the location of e1
						return new IRSeq(s1p, s2p,new IRMove(e1p, e2p));	
					} else {
						IRExpr e1 =((IRMem) ((IRMove) input).target()).expr();
						IRStmt s1p = ((IRESeq) Canonicalize(e1)).stmt();
						IRExpr e1p = ((IRESeq) Canonicalize(e1)).expr();
						IRStmt s2p = ((IRESeq) Canonicalize(((IRMove) input).source())).stmt();
						IRExpr e2p = ((IRESeq) Canonicalize(((IRMove) input).source())).expr();
						IRTemp t = new IRTemp("sb"); // sb must be fresh TODO		
						return new IRSeq(s1p, new IRMove(t, e1p), s2p, new IRMove(t, e2p));
					}
				} else {*/
					return input;
				}
					/* else {
					IRStmt s1p = ((IRESeq) Canonicalize(((IRMove) input).target())).stmt();
					IRExpr e1p = ((IRESeq) Canonicalize(((IRMove) input).target())).expr();
					IRStmt s2p = ((IRESeq) Canonicalize(((IRMove) input).source())).stmt();
					IRExpr e2p = ((IRESeq) Canonicalize(((IRMove) input).source())).expr();
					// only if e2 does not affect the location of e1
					return new IRSeq(s1p, s2p,new IRMove(e1p, e2p));

					
				}*/
				
			} else if (input instanceof IRExp) {
					IRStmt s = ((IRESeq) Canonicalize(((IRExp) input).expr())).stmt();
					IRExpr e = ((IRESeq) Canonicalize(((IRExp) input).expr())).expr();
					return s;
			} else if (input instanceof IRReturn) {
					List<IRExpr>  e = ((IRReturn) input).rets();
					List<IRStmt> sl = new ArrayList<IRStmt>();
					List<IRExpr>  el = new ArrayList<IRExpr>();
					for (IRExpr e1: e) {
						sl.add(((IRESeq) Canonicalize(e1)).stmt());
						el.add(((IRESeq) Canonicalize(e1)).expr());
					}
					sl.add(new IRReturn(el));
					return new IRSeq(sl);
			} else if (input instanceof IRCJump) {
				IRStmt s = ((IRESeq) Canonicalize(((IRCJump) input).cond())).stmt();
				IRExpr e = ((IRESeq) Canonicalize(((IRCJump) input).cond())).expr();
				String l1 = ((IRCJump) input).trueLabel();
				String l2 = ((IRCJump) input).falseLabel();
				return new IRSeq(s, new IRCJump(e,l1,l2));
			} else if (input instanceof IRJump) {
				IRStmt s = ((IRESeq) Canonicalize(((IRJump) input).target())).stmt();
				IRExpr e = ((IRESeq) Canonicalize(((IRJump) input).target())).expr();
				return new IRESeq(new IRSeq(s, new IRJump(e)), null);
			} else {
				return input;
			}
		}
		return input;
	}
	
	// Design not finished
	public static void IRRun() {
		
	}
}
