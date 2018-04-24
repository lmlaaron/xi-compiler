package bsa52_ml2558_yz2369_yh326.optimization.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.dataflow_analysis.AvailableCopyAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.AvailableExpressionAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.optimization.cse.CommonSubexpressionElimination.ModType;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
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
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class CopyPropagation {
        public static void DoCopyPropagation(IRNode irNode) {
            Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
            for (String name : funcMap.keySet()) {
                IRFuncDecl func = funcMap.get(name);
                DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(func);
                AvailableCopyAnalysis aca = new AvailableCopyAnalysis(cfg);
                DataflowAnalysisResult<IRStmt, Set<String>> acResult = aca.worklist();
                Map<IRStmt, Set<String>> modifications = getModifications(aca, acResult, cfg, func);
                ListIterator<IRStmt> it = ((IRSeq) func.body()).stmts().listIterator();
                while (it.hasNext()) {
                    IRStmt cur = it.next();
                    if (modifications.containsKey(cur)) {
                    		Set<String> str_set= modifications.get(cur);                    		
                                    		
                    		if ( cur instanceof IRMove ) {
                        		IRMove replace = (IRMove) cur;
                        		
                    			for ( String str: str_set) {
                        			String src = getSource(str);
                            		String des = getDestination(str);
                            		replace = ReplaceTempRHS(replace, src, des);
                    			}
                    			it.remove();
                    			it.add(replace);
                    		} else {
                    			IRStmt replace = cur;
                    			for ( String str: str_set) {
                        			String src = getSource(str);
                            		String des = getDestination(str);
                            		replace = ReplaceTemp(replace, src, des);
                    			}
                    			it.remove();
                    			it.add(replace);
                    		}
                    }
                }        	
            }
        }
        
        private static Map<IRStmt, Set<String>> getModifications(AvailableCopyAnalysis aca,
                DataflowAnalysisResult<IRStmt, Set<String>> acResult, 
                DirectedGraph<IRStmt> cfg, IRFuncDecl func) {
            Map<IRStmt, Set<String>> modification = new HashMap<>();
            for (IRStmt stmt : ((IRSeq) func.body()).stmts()) {
                Set<String> in = acResult.in.get(stmt);
                modification.put(stmt, in);
            }
            //System.out.println(modification);
            return modification;
        }
        
        /**
         * @param stmt String form of a IRMove(IRTemp(x), IRTemp(y))
         */
        static private String getSource(String stmt) {
        	    //System.out.println(stmt.substring(stmt.lastIndexOf("TEMP") +"TEMP".length()+1,  stmt.length()-3));
        		return stmt.substring(stmt.lastIndexOf("TEMP")+ "TEMP".length()+1,  stmt.length()-3);
        }
        
        /**
         * 
         * @param stmt String form of a IRMove(IRTemp(x), IRTemp(y))
         * @return
         */
        static private String getDestination(String stmt) {
        		//System.out.println(stmt.substring(stmt.indexOf("TEMP")+ "TEMP".length()+1, stmt.lastIndexOf("TEMP")-3));
        		return  stmt.substring(stmt.indexOf("TEMP")+ "TEMP".length()+1, stmt.lastIndexOf("TEMP")-3);
        }
        
        static private IRMove ReplaceTempRHS(IRMove replace, String src, String des) {
        		IRExpr RHS = replace.source();
        		IRExpr LHS = replace.target();
        		RHS = ReplaceTempExpr(RHS, src, des);
        		return new IRMove(LHS, RHS);
        }
        
        static private IRStmt ReplaceTemp(IRStmt replace, String src, String des) {
        		if ( replace instanceof IRExp) {
        			return new IRExp(ReplaceTempExpr(((IRExp) replace).expr(), src, des));
        		} else if ( replace instanceof IRReturn) {        			
        			List<IRExpr> ret = new ArrayList<>();
        			for ( IRExpr expr: ((IRReturn) replace).rets()) {
        				ret.add(ReplaceTempExpr(expr, src, des));
        			}
        			return new IRReturn(ret);
        		} else if ( replace instanceof IRJump) {
        			return replace;
        		} else if ( replace instanceof IRCJump) {
        			IRCJump ret = (IRCJump) replace;
        			return new IRCJump(ReplaceTempExpr(ret.cond(),src,des), ret.trueLabel(),ret.falseLabel());
        		}
        		return replace;
        }
        
        static private IRExpr ReplaceTempExpr(IRExpr replace, String src, String des) {
        		if ( replace instanceof IRCall ) {
        			IRCall ret = (IRCall) replace;
        			int i = 0;
        			for ( IRExpr arg: ret.args()) {
        				ret.args().set(i, ReplaceTempExpr(((IRCall) replace).args().get(i), src, des));
        				i++;
        			}
        			return ret;
        		} else if ( replace instanceof IRBinOp) {
        			return new IRBinOp(
        					((IRBinOp) replace).opType(),
        			ReplaceTempExpr(((IRBinOp) replace).left(), src, des), 
        			ReplaceTempExpr(((IRBinOp) replace).right(), src, des));
        		} else if ( replace instanceof IRConst) {
        			// do nothing
        			return replace;
        		} else if ( replace instanceof IRMem ) {
        			return new IRMem(
        					ReplaceTempExpr(((IRMem) replace).expr(), src, des));
        		} else if ( replace instanceof IRTemp) {
        			if (((IRTemp) replace).name().equals(des)) {
        				return new IRTemp(des);
        			}
        			return replace;
        		} else if ( replace instanceof IRESeq) {
        			return replace; // lowered code should not contain ESeq, thus return itself
        		}
        		return replace;
        }
}
