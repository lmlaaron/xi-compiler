package bsa52_ml2558_yz2369_yh326.optimization.register_allocation;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.LiveVariableAnalysis;
import bsa52_ml2558_yz2369_yh326.util.graph.*;

import java.util.*;

public class RegisterAllocation {
    public static void RegisterAllocation(Assembly assm) {
        DirectedGraph<AssemblyStatement> cfg = ControlFlowGraph.fromAssembly(assm);

        // run live variable analysis:
        LiveVariableAnalysis lva = new LiveVariableAnalysis(cfg);
        DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult = lva.worklist();

        System.out.println("Live Variable Analysis Result:");
        for (Set<String> tempGroup : lvResult.out.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");

            for (String s : tempGroup) {
                sb.append (s);
                sb.append(", ");
            }

            sb.append("}");

            System.out.println(sb.toString());
        }
        System.out.println();

        // construct interference graph:
        Graph<String> iGraph = constructInterferenceGraph(lvResult);

        System.out.println("Interference graph temps:");
        for (String t : iGraph.getVertices())
            System.out.println(t);
        System.out.println();


        System.out.println("Graph coloring...");

        // color the graph:
        Map<String, Integer> colorings = new HashMap<String, Integer>();
        HashSet<Integer> c = new HashSet<>();
        for (int i = 0; i < 15; i++) {
            c.add(i);
        }

        boolean fullyColored = new GraphColoring<String, Integer>(iGraph).colorBasic(c, colorings);
        if (fullyColored)
            System.out.println("All temps colored!");
        else
            System.out.println("Some Temps Weren't Colored");

        System.out.println("Colorings:");
        for (String temp : iGraph.getVertices()) {
            if (!colorings.containsKey(temp)) {
                System.out.println(temp + " - " + "NOT_COLORED");
            }
            else {
                System.out.println(temp + " - " + colorings.get(temp));
            }
        }
    }

    protected static Graph<String> constructInterferenceGraph
            (DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult) {

        Graph<String> interferenceGraph = new UndirectedGraph<>();

        for (Set<String> interferences : lvResult.in.values())
            addInterferences(interferences, interferenceGraph);

        for (Set<String> interferences : lvResult.out.values())
            addInterferences(interferences, interferenceGraph);

        return interferenceGraph;
    }

    protected static void addInterferences(Set<String> interferences, Graph<String> graph) {
        // each set is a set of interfering temps. connect them in the graph
        ArrayList<String> tempList = new ArrayList<String>(interferences);
        for (int i = 0; i < tempList.size(); i++) {
            graph.addVertex(tempList.get(i));
            for (int j = i+1; j < tempList.size(); j++) {
                graph.addEdge(tempList.get(i), tempList.get(j));
            }
        }
    }
}
