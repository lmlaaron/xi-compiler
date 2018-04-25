package bsa52_ml2558_yz2369_yh326.optimization.register_allocation;

import bsa52_ml2558_yz2369_yh326.assembly.*;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.LiveVariableAnalysis;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import bsa52_ml2558_yz2369_yh326.util.graph.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegisterAllocation {
    public static void RegisterAllocation(Assembly assm) {
        List<AssemblyFunction> functions = AssemblyUtils.partitionFunctions(assm);

        assm.statements = new LinkedList<>();
        for (AssemblyFunction f : functions) {

            // NOTE: we increase the value of stacksize by two and set the counter of rtable to 2
            // to compensate for a bug that is not currently understood. removing these countermeasures
            // will introduce bugs!!

            AssemblyUtils.systemVEnforce(f); // replace _ARGX, _RETX, __RETURN_X with appropriate register/stack location

            StackTable rTable = new StackTable(); // will be populated with all temps that were spilled to stack
            rTable.SetCounter(2);
            Map<String, String> colorings = new HashMap<>(); // map temp/register to register
            for (String r : Utilities.registersForAllocation()) { // registers are precolored
                colorings.put(r, r);
            }
            colorings = registerAllocation(f, rTable, colorings);

            sTableComment(rTable, f);

            // perform appropriate loads, saves according to system V
            f.calleeSave(colorings);
            f.callerSave(colorings);

            // calculate and specify stack size
            int stacksize = rTable.size() + AssemblyUtils.getMaxStackOverHeadForFunctionCalls(f.statements);
            if (stacksize % 2 != 0) stacksize++; // align to nearest 16 bytes
            stacksize+=2;
            stacksize *= 8;
            f.setStackSize(stacksize);

            assm.statements.addAll(f.statements);
        }
    }

    protected static void sTableComment(StackTable sTable, AssemblyFunction f) {
        if (!f.actuallyAFunction() || sTable.size() == 0) return;

        ListIterator<AssemblyStatement> it = f.statements.listIterator();

        // iterate to the function label
        while (true) {
            AssemblyStatement stmt = it.next();
            if (stmt.operation.length() >= 2 && stmt.operation.substring(0,2).equals("_I")) {
                it.previous();

                StringBuilder comment = new StringBuilder();
                comment.append("=== Stack Locations For " + f.getFunctionName() + " ===\n");
                for (String temp : sTable.stackTable.keySet()) {
                    comment.append(String.format("%-15s -> %d\n", temp, sTable.MemIndex(temp)*8));
                }

                for (AssemblyStatement commentPart : AssemblyStatement.comment(comment.toString()))
                    it.add(commentPart);

                return;
            }
        }
    }

    protected static Map<String, String> registerAllocation(Assembly assm, StackTable rTable, Map<String, String> preColorings) {
        // labels do not change, so we only need to find them once
        Set<String> labels = AssemblyUtils.collectLabels(assm, false);
        List<String> registers = Utilities.registersForAllocation();


        Set<String> mustColor = new HashSet<>();

        while (true) {
            HashMap<String, String> colorings = new HashMap<>(preColorings);

            System.out.println("===== ITERATION =====");

            DirectedGraph<AssemblyStatement> cfg = ControlFlowGraph.fromAssembly(assm);

            DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult = new LiveVariableAnalysis(cfg).worklist();

            Graph<String> interferenceG = constructInterferenceGraph(assm, lvResult, labels);
            for (String label : labels) // labels aren't temps
                interferenceG.removeVertex(label);
            interferenceG.removeVertex("STACKSIZE"); // this is a special marker that serves another purpose.

            // hardcoded fix for cases such as "call", where a register may have a def but no use.
            //  For registers, this should still mean that the register still interferes with any temps
            //  live at that point
            for (AssemblyStatement stmt : lvResult.in.keySet()) { // confusingly for reverse dataflow analysis, in is out
                // for each register defined by this statement,
                // force it to intersect with all temps that are live here
                AssemblyUtils.def(stmt).stream().filter(
                        entity -> Utilities.isRealRegister(entity)
                ).forEach(
                        register -> lvResult.in.get(stmt).stream().forEach(
                                interfering -> {
                                    interferenceG.addEdge(register, interfering);
                                    interferenceG.addEdge(interfering, register);
                                }
                        )
                );
            }


            // TODO: REMOVE
            System.out.println();
            System.out.println("TEMPS:");
            for (String temp : interferenceG.getVertices()) {
                System.out.println(temp);
            }
            System.out.println("");


            GraphColoring<String, String> gc = new GraphColoring<>(interferenceG);
            HashSet<String> spilled = new HashSet<>(gc.colorRestricted(registers, colorings, mustColor));

            if (spilled.isEmpty()) {
                // easy part. Allocate registers appropriately, as we have a proper allocation
                System.out.println("Allocated Registers for all temps!");

                ListIterator<AssemblyStatement> it = assm.statements.listIterator();
                while (it.hasNext()) {
                    AssemblyStatement stmt = it.next();
                    String originalStmt = stmt.toString();

                    for (AssemblyOperand op : stmt.operands) {
                        op.ResolveType();
                        op.setTemps(
                            // replace temps with 'color'
                            op.getTemps().stream().map(
                                t -> {
                                    if (colorings.containsKey(t)) return colorings.get(t);
                                    else return t;
                                }
                            ).collect(Collectors.toList())
                        );

                        // integrity check: make sure there aren't any temps:
                        for (String temp : op.getTemps()) {
                            if (!temp.equals("STACKSIZE") && !labels.contains(temp)){
                                System.out.println("WARN: REMAINING TEMP -> " + temp);
                                System.out.println("\tContained by Interference Graph: " + interferenceG.getVertices().contains(temp));
                            }
                        }
                    }

                    // commenting
                    if (!stmt.toString().equals(originalStmt)) {
                        it.previous();
                        for (AssemblyStatement commentPart : AssemblyStatement.comment(originalStmt))
                            it.add(commentPart);
                        it.next();
                    }

                }

                return colorings; // we're done
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
                                    String freshTemp = temp + "_COMPENSATOR";

                                    // this new temp's purpose is to compensate for a temp that was spilled
                                    // to the stack. It would be silly if we had to spill this one also
                                    mustColor.add(freshTemp);

                                    String stackOffset = Integer.toString(rTable.MemIndex(temp) * 8);
                                    AssemblyOperand stackLocation = AssemblyOperand.MemMinus("rbp", stackOffset);

                                    //TODO: remove
                                    System.out.println("Stack Location: " + stackLocation.value());

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
            (Assembly assm,
             DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult,
             Set<String> labels) {

        Graph<String> interferenceGraph = new UndirectedGraph<>();

        for (String r : Utilities.registersForAllocation())
            interferenceGraph.addVertex(r);

        //TODO: REMOVE
        // debug the live variable analysis output:
        Set<String> lvTemps = new HashSet<>();
        Stream.concat(lvResult.in.values().stream(), lvResult.out.values().stream()).forEach(
                s ->  lvTemps.addAll(s)
        );
        System.out.println();
        System.out.println("LIVE VARIABLE TEMPS:");
        for (String temp : lvTemps)
            System.out.println(temp);
        System.out.println();


        // There seem to be cases when live variable analysis doesn't catch all temps! Add the temps directly
        // from the assembly, just to be sure
        // TODO: Figure out why live variable analysis doesn't catch everything!
        for (AssemblyStatement stmt : assm.statements) {
            for (AssemblyOperand op : stmt.operands) {
                interferenceGraph.getVertices().addAll(op.getTemps());
            }
        }

        for (Set<String> interferences : lvResult.in.values())
            addInterferences(interferences, interferenceGraph, labels);

        for (Set<String> interferences : lvResult.out.values())
            addInterferences(interferences, interferenceGraph, labels);

        return interferenceGraph;
    }

    protected static void addInterferences(Set<String> liveTemps, Graph<String> graph, Set<String> labels) {
        // each set is a set of interfering temps (and labels). connect the temps in the graph
        ArrayList<String> tempList = new ArrayList<String>(liveTemps);
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
        Graph<String> iGraph = constructInterferenceGraph(assm, lvResult, AssemblyUtils.collectLabels(assm, false));

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
