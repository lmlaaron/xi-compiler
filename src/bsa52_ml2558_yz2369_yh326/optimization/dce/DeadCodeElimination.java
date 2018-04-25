package bsa52_ml2558_yz2369_yh326.optimization.dce;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DeadVariableAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
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

public class DeadCodeElimination {
	private static Map<IRStmt, Integer> useCountTable;
	/**
	 * Perform DeadVariableAnalysis, for each node count the the number of use
	 * remove IRMove nodes with zero use
	 * @param irNode
	 */
	public static void DoDeadCodeElimination(IRNode irNode) {
		useCountTable = new HashMap<IRStmt,Integer>();
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            IRFuncDecl func = funcMap.get(name);
            DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(func);
            DeadVariableAnalysis dva = new DeadVariableAnalysis(cfg);
            DataflowAnalysisResult<IRStmt,Set<IRStmt>> dvResult =dva.worklist();
            
            // all reference count are set to zero initally
            for( IRStmt node: cfg.getVertices()) {
            		useCountTable.put(node, 0);
            }
            Map<IRStmt, Set<IRStmt>> modifications = getModifications(dva, dvResult, cfg, func);
            // for each node, if the node is defined in the in edge and refered, increment the count
            ListIterator<IRStmt> it = ((IRSeq) func.body()).stmts().listIterator();
            
            //for each node in CFG, check the respective in(n)
            while (it.hasNext()) {				
            		IRStmt cur = it.next();
            		System.out.println("current node:" + cur.toString());
            		if ( modifications.containsKey(cur)) {
            			Set<IRStmt> modification = modifications.get(cur);
            			
            				// for each value in the incoming edge, check if it is used in the node or not
            				for ( IRStmt in: modification ) {
            					//System.out.println("in set                    :" + in.toString());
            					if ( in instanceof IRMove && ((IRMove) in).target() instanceof IRTemp ) {
            						boolean occur = CheckOccuranceStmt(cur, ((IRTemp) ((IRMove)in).target()));
            						if ( occur ) {
            							//System.out.println("in string:"+in.toString());
            							//System.out.println("current ir:" + cur.toString());
            							assert useCountTable.get(in) == 0;
            							useCountTable.put(in, useCountTable.get(in)+1);
            						}
            					}
            				}
            		}
            }
            
            // remove the nodes with useCount 0
            boolean isUseCountChange = true;
            while (isUseCountChange) {
            	isUseCountChange = false;
            it = ((IRSeq) func.body()).stmts().listIterator();
            while (it.hasNext()) {
            	    //System.out.println("in set                    :" + in.toString());
 
            		IRStmt cur = it.next();
                   	System.out.println("current node:" + cur.toString());
                	System.out.println("use count: "+ useCountTable.get(cur));
            		// findout IRMove that is not used
            		if ( cur instanceof IRMove &&
            				((IRMove) cur).target() instanceof IRTemp &&
            				useCountTable.containsKey(cur) &&
            				useCountTable.get(cur) == 0) {
            					isUseCountChange = true;  // delete something, need to run another round
            					
            					// UPDATE the useCountTable for entries that define TEMP used by the deleted node
            					ListIterator<IRStmt> it_table = ((IRSeq) func.body()).stmts().listIterator();
            					while ( it_table.hasNext()) {
            						IRStmt it_table_cur = it_table.next();
            						if ( it_table_cur instanceof IRMove &&
            								((IRMove) it_table_cur).target() instanceof IRTemp &&
            								useCountTable.containsKey(it_table_cur) &&
            								useCountTable.get(it_table_cur) > 0 &&
            								CheckOccuranceStmt(cur, (IRTemp) ((IRMove) it_table_cur).target())) {
            							useCountTable.put(it_table_cur, useCountTable.get(it_table_cur)-1);
            							if (useCountTable.get(it_table_cur) == 0) {
            								//it_table.remove();
            							}
            						}
            					}
            					
            					// remove the current node 
            					it.remove(); 
            		}
            }
            }
        }
	}

	/**
	 * 
	 * @param stmt
	 * @param temp
	 * @return true if stmt uses the temp (i.e., not on LHS)
	 */
	private static boolean CheckOccuranceStmt(IRStmt stmt, IRTemp temp) {
		if ( stmt instanceof IRMove) {
			return CheckOccuranceExpr(((IRMove) stmt).source(), temp);
		} else if ( stmt instanceof IRExp) {
			return CheckOccuranceExpr(((IRExp) stmt).expr(), temp);
		} else if ( stmt instanceof IRReturn) {
			for ( IRExpr expr: ((IRReturn) stmt).rets()) {
				if ( CheckOccuranceExpr(expr, temp) ) return true;
			}
			return false;
		} else if (stmt instanceof IRJump) {
		} else if ( stmt instanceof IRCJump) {
			return CheckOccuranceExpr(((IRCJump) stmt).cond(), temp);
		}
		return false;
	}
	
	/**
	 * 
	 * @param expr
	 * @param temp
	 * @return true if expr contains temp
	 */
	private static boolean CheckOccuranceExpr(IRExpr expr, IRTemp temp) {
		if ( expr instanceof IRCall) {
			for ( IRExpr e: ((IRCall) expr).args()) {
				if ( CheckOccuranceExpr(e, temp)) return true;
			}
			return false;
		} else if ( expr  instanceof IRBinOp) {
			return CheckOccuranceExpr(((IRBinOp) expr).left(), temp) || CheckOccuranceExpr(((IRBinOp) expr).right(), temp);
		} else if ( expr instanceof IRTemp ) {
			return temp.name().equals(((IRTemp) expr).name()); // TODO reference equality or value equality?
		} else if ( expr instanceof IRMem ) {
			return CheckOccuranceExpr(((IRMem) expr).expr(), temp);
		} else if ( expr instanceof IRESeq) {
			return CheckOccuranceExpr(((IRESeq) expr).expr(), temp) || CheckOccuranceStmt( ((IRESeq) expr).stmt(), temp);
		}
		return false;
	}
	
    
    private static Map<IRStmt, Set<IRStmt>> getModifications(DeadVariableAnalysis dva,
            DataflowAnalysisResult<IRStmt, Set<IRStmt>> acResult, 
            DirectedGraph<IRStmt> cfg, IRFuncDecl func) {
        Map<IRStmt, Set<IRStmt>> modification = new HashMap<>();
        for (IRStmt stmt : ((IRSeq) func.body()).stmts()) {
            Set<IRStmt> in = acResult.in.get(stmt);
            modification.put(stmt, in);
        }
        //System.out.println(modification);
        return modification;
    }
    
   
}