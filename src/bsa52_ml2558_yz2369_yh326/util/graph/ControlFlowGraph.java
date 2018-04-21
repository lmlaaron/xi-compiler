package bsa52_ml2558_yz2369_yh326.util.graph;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ControlFlowGraph {

    /**
     * Note: because ret statements move control flow to an undefined
     * location, a ret operation will not be connected to any other nodes
     *
     * @param assm
     * @return
     */
    public static DirectedGraph<AssemblyStatement> fromAssembly (Assembly assm) {
        if (assm.incomplete()) {
            throw new RuntimeException("Assembly must be complete to form a CFG");
        }

        // arraylist allows for easier access by index
        ArrayList<AssemblyStatement> statements = new ArrayList<>(assm.statements);

        DirectedGraph<AssemblyStatement> g = new DirectedGraph<>("Assembly Control Flow DirectedGraph");

        HashSet<String> labels = AssemblyUtils.collectLabels(assm, true);

        // collect mapping of label name to location by index
        HashMap<String, Integer> labelToIndex = new HashMap<String, Integer>();
        for (int i = 0; i < statements.size(); i++) {
            AssemblyStatement stmt = statements.get(i);

            if (labels.contains(stmt.operation)) {
                // in the map, remove the colon from the label name
                // because we're trying to match it as an argument of jump statements
                labelToIndex.put(stmt.operation.substring(0, stmt.operation.length()-1), i);
            }
        }

        // error checking...
        if (labelToIndex.size() != labels.size()) { throw new RuntimeException("Something went wrong!"); }

        // iterate through statements again to build up graph
        for (int i = 0; i < statements.size(); i++) {

            AssemblyStatement statement = statements.get(i);

            boolean lastStatement = i == statements.size()-1;

            // some sort of jump
            if (statement.operation.substring(0,1).equals("j")) {
                AssemblyStatement labelStatement = statements.get(labelToIndex.get(statement.operands[0].value()));

                // unconditional
                if (statement.operation.equals("jmp")) {
                    g.addEdge(statement, labelStatement);
                }
                // conditional
                else {
                    g.addEdge(statement, labelStatement);
                    if (!lastStatement) g.addEdge(statement, statements.get(i+1));
                }
            }
            // ret : for now, we'll say it doesn't connect to anything
            else if (statement.operation.equals("ret")) {
                // do nothing...
            }
            // default case: control falls through to next statement
            // TODO: how to handle 'call'?
            else {
                if (!lastStatement) g.addEdge(statement, statements.get(i+1));
            }
        }

        return g;
    }
}
