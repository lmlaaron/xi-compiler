package bsa52_ml2558_yz2369_yh326.optimization.cse;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.dataflow_analysis.AvailableExpressionAnalysis;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

public class CommonSubexpressionElimination {
    public static void DoCSE(IRNode irNode) {
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            DirectedGraph<IRStmt> cfg = ControlFlowGraph.fromIRFuncDecl(funcMap.get(name));
            //AvailableExpressionAnalysis aea = new AvailableExpressionAnalysis(cfg);
            //DataflowAnalysisResult<IRStmt, Set<String>> aeResult = aea.worklist();
        }
    }
}
