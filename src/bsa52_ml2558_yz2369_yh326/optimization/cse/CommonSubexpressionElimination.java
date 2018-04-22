package bsa52_ml2558_yz2369_yh326.optimization.cse;

import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.dataflow_analysis.AvailableExpressionAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class CommonSubexpressionElimination {
    public static void DoCSE(IRNode irNode) {
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(funcMap.get(name));
            AvailableExpressionAnalysis aea = new AvailableExpressionAnalysis(cfg);
            DataflowAnalysisResult<IRStmt, Set<String>> aeResult = aea.worklist();
            for (IRStmt stmt : cfg.getVertices()) {
                Set<String> in = aeResult.in.get(stmt);
                Set<String> out = aeResult.out.get(stmt);
                System.out.println("NODE:    " + stmt);
                in.forEach(st -> System.out.println("  in   " + st));
                out.forEach(st -> System.out.println("  out  " + st));
                if (stmt instanceof IRMove && ((IRMove) stmt).target() instanceof IRTemp) {
                    IRExpr source = ((IRMove) stmt).source();
                    if (source instanceof IRBinOp || source instanceof IRMem) {
                        if (in.contains(source.toString())) {
                            System.out.println("Found duplicate");
                        }
                    }
                }
            }
        }
    }
}
