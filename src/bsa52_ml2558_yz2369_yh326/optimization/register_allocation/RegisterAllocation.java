package bsa52_ml2558_yz2369_yh326.optimization.register_allocation;

import bsa52_ml2558_yz2369_yh326.assembly.*;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.LiveVariableAnalysis;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import bsa52_ml2558_yz2369_yh326.util.graph.*;

import java.util.*;
import java.util.stream.Collectors;

import static bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand.MemMinus;

public class RegisterAllocation {
    public static void RegisterAllocation(Assembly assm) {

        // labels do not change, so we only need to find them once
        Set<String> labels = AssemblyUtils.collectLabels(assm, false);

        List<String> registers = Utilities.registersForAllocation();

        RegisterTable rTable = new RegisterTable();

        Map<String, String> precolorings = new HashMap<>(); // TODO: remove?

        Set<String> mustColor = new HashSet<>();

        // TODO: this should be done per function, not to the whole assembly at once!
        while (true) {
            DirectedGraph<AssemblyStatement> cfg = ControlFlowGraph.fromAssembly(assm);
            LiveVariableAnalysis lva = new LiveVariableAnalysis(cfg);
            DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult = lva.worklist();

            Graph<String> interferenceG = constructInterferenceGraph(lvResult, labels);
            for (String label : labels) // labels aren't temps
                interferenceG.removeVertex(label);

            Map<String, String> colorings = new HashMap(precolorings);

            GraphColoring<String, String> gc = new GraphColoring<>(interferenceG);
            HashSet<String> spilled = new HashSet<>(gc.colorRestricted(registers, colorings, mustColor));

            if (spilled.isEmpty()) {
                // easy part. Allocate registers appropriately, as we have a proper allocation
                System.out.println("Allocated Registers for all temps!");
                return;
            }
            else {
                System.out.println("Didn't allocate registers for all temps!");
                System.out.println("Spilled Temps:");
                for (String t : spilled)
                    System.out.println(t);
                System.out.println();

                // 1) assign a space on stack for each spilled temp
                for (String t : spilled) {
                    rTable.add(t);
                }
                // 2) precede each usage with loads, follow with writes
                ListIterator<AssemblyStatement> it = assm.statements.listIterator();
                while (it.hasNext()) {
                    AssemblyStatement stmt = it.next();

                    List<AssemblyStatement> loads = new LinkedList<>();
                    List<AssemblyStatement> saves = new LinkedList<>();

                    for (int i = 0; i < stmt.operands.length; i++) {
                        AssemblyOperand op = stmt.operands[i];
                        op.ResolveType();

                        // replace spilled temps with a fresh temp which reads their value from the stack
                        // before the current operation and writes to it immediately after
                        List<String> temps = op.getTemps();
                        temps = temps.stream().map(
                            temp -> {
                                if (spilled.contains(temp)) {
                                    // create a new temp to handle reads and writes for this statement
                                    String freshTemp = Utilities.freshTemp();

                                    // this new temp's purpose is to compensate for a temp that was spilled
                                    // to the stack. It would be silly if we had to spill this one also
                                    mustColor.add(freshTemp);

                                    String stackOffset = Integer.toString(rTable.MemIndex(temp) * 8);
                                    AssemblyOperand stackLocation = AssemblyOperand.MemMinus("rbp", stackOffset);

                                    // TODO: this is excessive! There are some cases where we could just load, or just save
                                    loads.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), stackLocation));
                                    saves.add(new AssemblyStatement("mov", stackLocation, new AssemblyOperand(freshTemp)));

                                    return freshTemp;
                                }
                                else {
                                    return temp;
                                }
                            }
                        ).collect(Collectors.toList());
                        op.setTemps(temps);
                    }

                    // add new read, write instructions:
                    if (!loads.isEmpty() || !saves.isEmpty()) {
                        it.remove();

                        for (AssemblyStatement load : loads)
                            it.add(load);
                        it.add(stmt);
                        for (AssemblyStatement save : saves)
                            it.add(save);
                    }
                }
            }
        }
    }

    protected static Graph<String> constructInterferenceGraph
            (DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult, Set<String> labels) {

        Graph<String> interferenceGraph = new UndirectedGraph<>();

        for (Set<String> interferences : lvResult.in.values())
            addInterferences(interferences, interferenceGraph, labels);

        for (Set<String> interferences : lvResult.out.values())
            addInterferences(interferences, interferenceGraph, labels);

        return interferenceGraph;
    }

    protected static void addInterferences(Set<String> interferences, Graph<String> graph, Set<String> labels) {
        // each set is a set of interfering temps (and labels). connect the temps in the graph
        ArrayList<String> tempList = new ArrayList<String>(interferences);
        for (int i = 0; i < tempList.size(); i++) {
            String ti = tempList.get(i);
            if (labels.contains(ti))
                continue;
            graph.addVertex(ti);
            for (int j = i+1; j < tempList.size(); j++) {
                String tj = tempList.get(j);
                if (labels.contains(tj))
                    continue;
                graph.addVertex(tj);
                graph.addEdge(ti, tj);
            }
        }
    }

    protected static void debug(Assembly assm) {
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
        Graph<String> iGraph = constructInterferenceGraph(lvResult, AssemblyUtils.collectLabels(assm, false));

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

        List<String> notColored = new GraphColoring<String, Integer>(iGraph).colorBasic(c, colorings);
        if (notColored.isEmpty())
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
}
