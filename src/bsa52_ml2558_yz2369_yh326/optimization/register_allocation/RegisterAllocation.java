package bsa52_ml2558_yz2369_yh326.optimization.register_allocation;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyFunction;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyUtils;
import bsa52_ml2558_yz2369_yh326.assembly.StackTable;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.DataflowAnalysisResult;
import bsa52_ml2558_yz2369_yh326.dataflow_analysis.LiveVariableAnalysis;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.Graph;
import bsa52_ml2558_yz2369_yh326.util.graph.GraphColoring;
import bsa52_ml2558_yz2369_yh326.util.graph.UndirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RegisterAllocation {

    public static void DoREG(Assembly assm) {
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
            // extra adjustment for the six callee-save registers
            stacksize += 8*8;
            f.setStackSize(stacksize);

            assm.statements.addAll(f.statements);
        }
    }

    protected static void sTableComment(StackTable sTable, AssemblyFunction f) {
        if (!Settings.asmComments) return;
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


        Set<String> cantSpill = new HashSet<>();
        cantSpill.addAll(Utilities.allRegisters());

        while (true) {
            HashMap<String, String> colorings = new HashMap<>(preColorings);

            // System.out.println("===== ITERATION =====");

            DirectedGraph<AssemblyStatement> cfg = ControlFlowGraph.fromAssembly(assm);

            DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult = new LiveVariableAnalysis(cfg).worklist();


            Graph<String> interferenceG = constructInterferenceGraph(assm, lvResult, labels);

            for (String label : labels) // labels aren't temps
                interferenceG.removeVertex(label);
            interferenceG.removeVertex("STACKSIZE"); // this is a special marker that serves another purpose.

            // hardcoded fix for cases where a temp or register has definitions but no uses. In such cases it can interfere
            // with some other value that has been allocated the same register, even though technically it's never 'live'
            // TODO: for any temp (not register) that's never live, just remove its definitions?
            for (AssemblyStatement stmt : lvResult.in.keySet()) { // confusingly for reverse dataflow analysis, in is out
                AssemblyUtils.def(stmt).stream().filter(
                        entity -> Utilities.isRealRegister(entity) || !Utilities.isNumber(entity)
                ).forEach(
                        entity -> lvResult.in.get(stmt).stream().forEach(
                                interfering -> {
                                    if (!entity.equals(interfering)) {
                                        interferenceG.addEdge(entity, interfering);
                                        interferenceG.addEdge(interfering, entity);
                                    }
                                }
                        )
                );
            }

            int numColors = Utilities.registersForAllocation().size();
            while (moveCoalesce(assm, interferenceG, numColors-1)) {}

            GraphColoring<String, String> gc = new GraphColoring<>(interferenceG);
            HashSet<String> spilled = new HashSet<>(gc.colorRestricted(registers, colorings, cantSpill));

            if (spilled.isEmpty()) {
                // easy part. Allocate registers appropriately, as we have a proper allocation
                //System.out.println("Allocated Registers for all temps!");


                colorings.remove("STACKSIZE");
                //System.out.printf("Colorings Contains STACKSIZE : %b%n", colorings.containsKey("STACKSIZE"));

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

//                        // integrity check: make sure there aren't any temps:
//                        for (String temp : op.getTemps()) {
//                            if (!temp.equals("STACKSIZE") && !labels.contains(temp)){
//                                System.out.println("WARN: REMAINING TEMP -> " + temp);
//                                System.out.println("\tContained by Interference Graph: " + interferenceG.getVertices().contains(temp));
//                            }
//                        }
                    }

                    // commenting
                    if (!stmt.toString().equals(originalStmt) /*&& Settings.asmComments*/) {
                        it.previous();
                        for (AssemblyStatement commentPart : AssemblyStatement.comment(originalStmt))
                            it.add(commentPart);
                        it.next();
                    }

                }

                return colorings; // we're done
            }
            else {
//                System.out.println("Didn't allocate registers for all temps!");
//                System.out.println("Spilled Temps:");
//                for (String t : spilled)
//                    System.out.println(t);
//                System.out.println();

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
                                    cantSpill.add(freshTemp);

                                    String stackOffset = Integer.toString(rTable.MemIndex(temp) * 8);
                                    AssemblyOperand stackLocation = AssemblyOperand.MemMinus("rbp", stackOffset);

//                                    //TODO: remove
//                                    System.out.println("Stack Location: " + stackLocation.value());

                                    // TODO: this is excessive! There are some cases where we could just load, or just save
                                    if (AssemblyUtils.use(stmt).contains(temp))
                                        loads.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp), stackLocation));
                                    if (AssemblyUtils.def(stmt).contains(temp))
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

    protected static boolean moveCoalesce(Assembly assm, Graph<String> interferenceG, int maxInDegree) {
        Map<String, String> replaceWith = new HashMap<String, String>();
        HashSet<String> nono = new HashSet<String>();

        ListIterator<AssemblyStatement> it = assm.statements.listIterator();
        while (it.hasNext()) {
            AssemblyStatement stmt = it.next();

            if (stmt.operation.equals("mov")) {
                boolean validPair = true;

                int numRegisters = 0;
                for (AssemblyOperand op : stmt.operands) {
                    if (op.type == AssemblyOperand.OperandType.MEM) {
                        //System.out.printf("%-20s is a Memory Operand%n", op);
                        validPair = false;
                    }
                    else if (op.value().charAt(0) == '[') {
                        validPair = false; // the previous check doesn't seem to work in all cases. WHY?
                    }
                    else if (Utilities.isNumber(op.value())) {
                        validPair = false;
                        //System.out.printf("%-20s is a Number%n", op);
                    }
                    if (Utilities.isRealRegister(op.value())) numRegisters++;
                }
                if (numRegisters >= 2) {
                    //System.out.printf("Both operands were registers%n");
                    validPair = false;
                }

                String a = stmt.operands[0].value();
                String b = stmt.operands[1].value();

                if (nono.contains(a) || nono.contains(b)) {
                    validPair = false;
                    //System.out.printf("NONO%n");
                }
                else if (a.equals(b)) {
                    //System.out.printf("Operands are equal: %s %s%n", a, b);
                    validPair = false;
                }
                else if (interferenceG.getSuccessors(a).contains(b) || interferenceG.getSuccessors(b).contains(a)) {
                    //System.out.printf("Operands Interfere!%n");
                    validPair = false;
                }

                if (validPair) {
                    // the union of the set of thing interfering with a and b must be <= maxInDegree in size
                    HashSet<String> union = new HashSet<>(interferenceG.getSuccessors(a));
                    union.addAll(interferenceG.getSuccessors(b));

                    if (union.size() > maxInDegree) {
                        //System.out.printf("Collective in-degree is %d%n", union.size());
                        continue;
                    }
                    else {
                        // replace a with b
                        if (Utilities.isRealRegister(a)) {
                            String temp = a;
                            a = b;
                            b = temp;
                        }
                        //System.out.printf("Replace %-20s with %20s%n", a, b);
                        replaceWith.put(a, b);
                        // add a's interferences to b
                        int before = interferenceG.getSuccessors(b).size();
                        LinkedList<String> succ = new LinkedList<>(interferenceG.getSuccessors(a));
                        for (String aSucc : succ)
                            interferenceG.addEdge(aSucc, b);
                        int after = interferenceG.getSuccessors(b).size();
                        //System.out.printf("Merge set: %d -> %d%n", before, after);
                        interferenceG.removeVertex(a);

                        boolean b1 = interferenceG.getVertices().contains(a);
                        boolean b2 = interferenceG.getEdges().containsKey(a);
                        String af = a;
                        boolean b3 = interferenceG.getEdges().values().stream().anyMatch(
                                s -> s.contains(af)
                        );
                        //System.out.printf("INTEGRITY CHECKS: %b %b %b%n", b1, b2, b3);

                        it.remove();

                        nono.add(a);
                        nono.add(b);
                    }
                }
            }
        }

        if (nono.isEmpty()) {
            //System.out.println("couldn't coalesce ANYTHING");
            return false;
        }
        else {
            // we have a set of temps to be replaced
            for (AssemblyStatement stmt : assm.statements) {
                for (AssemblyOperand op : stmt.operands) {
                    op.setEntities(
                        op.getEntities().stream().map(
                            e ->  {
                                if (replaceWith.containsKey(e)) return replaceWith.get(e);
                                else return e;
                            }
                        ).collect(Collectors.toList())
                    );
                }
            }
            return true;
        }
    }

    protected static Graph<String> constructInterferenceGraph
            (Assembly assm,
             DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult,
             Set<String> labels) {

        Graph<String> interferenceGraph = new UndirectedGraph<>();

        for (String r : Utilities.registersForAllocation())
            interferenceGraph.addVertex(r);


        // There seem to be cases when live variable analysis doesn't catch all temps! Add the temps directly
        // from the assembly, just to be sure
        // TODO: Figure out why live variable analysis doesn't catch everything!
        for (AssemblyStatement stmt : assm.statements) {
            for (AssemblyOperand op : stmt.operands) {
                interferenceGraph.getVertices().addAll(op.getTemps().stream().filter(
                        t -> !labels.contains(t) && !t.equals("STACKSIZE")
                ).collect(Collectors.toList()));
            }
        }

        for (Set<String> interferences : lvResult.in.values())
            addInterferences(interferences, interferenceGraph, labels);

        for (Set<String> interferences : lvResult.out.values())
            addInterferences(interferences, interferenceGraph, labels);

        /*
        System.out.println("Interference Graph Debugging:");
        for (String k : interferenceGraph.getVertices()) {
            StringBuilder sb = new StringBuilder();
            for  (String adj : interferenceGraph.getSuccessors(k))
                sb.append(adj + " ");
            System.out.printf("%-23s {%s}%n", k, sb);
        }
        System.out.println();
        */

        return interferenceGraph;
    }

    protected static void addInterferences(Set<String> liveTemps, Graph<String> graph, Set<String> labels) {
        // each set is a set of interfering temps (and labels). connect the temps in the graph
        ArrayList<String> tempList = new ArrayList<String>(liveTemps);
        for (int i = 0; i < tempList.size(); i++) {
            String ti = tempList.get(i);
            if (labels.contains(ti) || ti.equals("STACKSIZE"))
                continue;
            graph.addVertex(ti);
            for (int j = i+1; j < tempList.size(); j++) {
                String tj = tempList.get(j);
                if (labels.contains(tj) || tj.equals("STACKSIZE"))
                    continue;
                graph.addVertex(tj);
                graph.addEdge(ti, tj);
                //System.out.printf("Adding interference edge %s <-> %s%n",ti, tj);
            }
        }
    }

    protected static void debug(Assembly assm) {
        DirectedGraph<AssemblyStatement> cfg = ControlFlowGraph.fromAssembly(assm);

        // run live variable analysis:
        LiveVariableAnalysis lva = new LiveVariableAnalysis(cfg);
        DataflowAnalysisResult<AssemblyStatement, Set<String>> lvResult = lva.worklist();

//        System.out.println("Live Variable Analysis Result:");
//        for (Set<String> tempGroup : lvResult.out.values()) {
//            StringBuilder sb = new StringBuilder();
//            sb.append("{");
//
//            for (String s : tempGroup) {
//                sb.append (s);
//                sb.append(", ");
//            }
//
//            sb.append("}");
//
//            System.out.println(sb.toString());
//        }
//        System.out.println();

        // construct interference graph:
        Graph<String> iGraph = constructInterferenceGraph(assm, lvResult, AssemblyUtils.collectLabels(assm, false));

//        System.out.println("Interference graph temps:");
//        for (String t : iGraph.getVertices())
//            System.out.println(t);
//        System.out.println();


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
