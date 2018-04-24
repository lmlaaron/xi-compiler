package bsa52_ml2558_yz2369_yh326.optimization.cse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.dataflow_analysis.AvailableExpressionAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class CommonSubexpressionElimination {
    
    public enum ModType {
        NEWVAR, USEVAR
    }
    
    /** Perform Common Subexpression Elimination
     * @param irNode the IRNode to do CSE on
     */
    public static void DoCSE(IRNode irNode) {
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            // For each function, construct graph, do analysis, get analysis result.
            IRFuncDecl func = funcMap.get(name);
            DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(func);
            AvailableExpressionAnalysis aea = new AvailableExpressionAnalysis(cfg);
            DataflowAnalysisResult<IRStmt, Set<String>> aeResult = aea.worklist();
            
            // Process analysis result to get all modifications to be done.
            Map<IRStmt, List<Tuple<ModType, String>>> modifications = getModifications(aea, aeResult, cfg, func);
            ListIterator<IRStmt> it = ((IRSeq) func.body()).stmts().listIterator();
            while (it.hasNext()) {
                IRStmt cur = it.next();
                // If current IRStmt is in map, need to modify.
                if (modifications.containsKey(cur)) {
                    List<Tuple<ModType, String>> mods = modifications.get(cur);
                    IRMove move = (IRMove) cur;
                    for (Tuple<ModType, String> mod : mods) {
                        it.remove();
                        if (mod.t1 == ModType.USEVAR) {
                            it.add(new IRMove(move.target(), new IRTemp(mod.t2)));
                        } else {
                            it.add(new IRMove(new IRTemp(mod.t2), move.source()));
                            it.add(new IRMove(move.target(), new IRTemp(mod.t2)));
                        }
                        move = (IRMove) it.previous();
                    }
                }
            }
        }
        //System.out.println(irNode);
    }
    
    /** Process analysis result to get all modifications to be done.
     * @param aea The Available Expression Analysis instance
     * @param aeResult The analysis result
     * @param cfg The directed control flow graph
     * @param func The current function that is performing CSE
     * @return All modifications needs to be done for each IRStmt in the map.
     * The value is the list of all modification for the specific IRStmt.
     */
    private static Map<IRStmt, List<Tuple<ModType, String>>> getModifications(AvailableExpressionAnalysis aea,
            DataflowAnalysisResult<IRStmt, Set<String>> aeResult, 
            DirectedGraph<IRStmt> cfg, IRFuncDecl func) {
        Map<IRStmt, List<Tuple<ModType, String>>> modification = new HashMap<>();
        for (IRStmt stmt : ((IRSeq) func.body()).stmts()) {
            Set<String> in = aeResult.in.get(stmt);
            if (stmt instanceof IRMove && ((IRMove) stmt).source() instanceof IRBinOp && 
                    in.contains(((IRMove) stmt).source().toString())) {
                List<IRStmt> origins = findOrigin(aea, cfg, stmt);
                String newTemp = "_temp_" + NumberGetter.uniqueNumber();
                for (IRStmt origin : origins) {
                    if (modification.containsKey(origin)) {// and type must be USEVAR
                        modification.get(origin).add(new Tuple<>(ModType.NEWVAR, newTemp));
                    } else {
                        List<Tuple<ModType, String>> mods = new ArrayList<>();
                        mods.add(new Tuple<>(ModType.NEWVAR, newTemp));
                        modification.put(origin, mods);
                    }
                }
                List<Tuple<ModType, String>> mods = new ArrayList<>();
                mods.add(new Tuple<>(ModType.USEVAR, newTemp));
                modification.put(stmt, mods);
            }
        }
        //System.out.println(modification);
        return modification;
    }
    
    /** Find the list of IRStmt that are the most recent predecessors that
     * generated the expression used in the given {@code stmt}. There can be
     * multiple if the statement has multiple predecessors and all of them
     * generated the expression.
     * @param aea The Available Expression Analysis instance
     * @param cfg The directed control flow graph
     * @param stmt The IRStmt whose expression's origin wants to be found
     * @return The list of IRStmt that are the most recent predecessors that
     * generated the expression used in the given {@code stmt}.
     */
    private static List<IRStmt> findOrigin(AvailableExpressionAnalysis aea,
            DirectedGraph<IRStmt> cfg, IRStmt stmt) {
        String source = ((IRMove) stmt).source().toString();
        List<IRStmt> origins = new ArrayList<IRStmt>();
        Set<IRStmt> iterated = new HashSet<IRStmt>();
        Set<IRStmt> queue = cfg.getPredecessors(stmt);
        while (!queue.isEmpty()) {
            Set<IRStmt> newQueue = new HashSet<IRStmt>();
            for (IRStmt curStmt : queue) {
                iterated.add(curStmt);
                if (aea.gen(curStmt).contains(source)) {
                    origins.add(curStmt);
                } else {
                    newQueue.addAll(cfg.getPredecessors(curStmt));
                }
            }
            newQueue.removeAll(iterated);
            queue = newQueue;
        }
        return origins;
    }
    
}
