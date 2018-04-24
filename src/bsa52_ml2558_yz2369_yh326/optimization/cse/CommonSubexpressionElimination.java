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
    
    public static void DoCSE(IRNode irNode) {
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            IRFuncDecl func = funcMap.get(name);
            DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(func);
            AvailableExpressionAnalysis aea = new AvailableExpressionAnalysis(cfg);
            DataflowAnalysisResult<IRStmt, Set<String>> aeResult = aea.worklist();
            Map<IRStmt, Tuple<ModType, String>> modifications = getModifications(aea, aeResult, cfg, func);
            ListIterator<IRStmt> it = ((IRSeq) func.body()).stmts().listIterator();
            while (it.hasNext()) {
                IRStmt cur = it.next();
                if (modifications.containsKey(cur)) {
                    it.remove();
                    IRMove move = (IRMove) cur;
                    Tuple<ModType, String> mod = modifications.get(cur);
                    if (mod.t1 == ModType.USEVAR) {
                        it.add(new IRMove(move.target(), new IRTemp(mod.t2)));
                    } else {
                        it.add(new IRMove(new IRTemp(mod.t2), move.source()));
                        it.add(new IRMove(move.target(), new IRTemp(mod.t2)));
                    }
                }
            }
        }
        //System.out.println(irNode);
    }
    
    private static Map<IRStmt, Tuple<ModType, String>> getModifications(AvailableExpressionAnalysis aea,
            DataflowAnalysisResult<IRStmt, Set<String>> aeResult, 
            DirectedGraph<IRStmt> cfg, IRFuncDecl func) {
        Map<IRStmt, Tuple<ModType, String>> modification = new HashMap<>();
        for (IRStmt stmt : ((IRSeq) func.body()).stmts()) {
            Set<String> in = aeResult.in.get(stmt);
            if (stmt instanceof IRMove && ((IRMove) stmt).source() instanceof IRBinOp && 
                    in.contains(((IRMove) stmt).source().toString())) {
                List<IRStmt> origins = findOrigin(aea, cfg, stmt);
                //System.out.println("The origin of stmt" + stmt + " are " + origins);
                String newTemp = "_temp_" + NumberGetter.uniqueNumberStr();
                origins.forEach(origin -> modification.put(origin, new Tuple<>(ModType.NEWVAR, newTemp)));
                modification.put(stmt, new Tuple<>(ModType.USEVAR, newTemp));
            }
        }
        //System.out.println(modification);
        return modification;
    }
    
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
