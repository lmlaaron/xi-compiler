package bsa52_ml2558_yz2369_yh326.util.graph;

import bsa52_ml2558_yz2369_yh326.assembly.Assembly;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyUtils;
import edu.cornell.cs.cs4120.xic.ir.IRCJump;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRJump;
import edu.cornell.cs.cs4120.xic.ir.IRLabel;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

        // iterate through statements again to build up graph
        for (int i = 0; i < statements.size(); i++) {

            AssemblyStatement statement = statements.get(i);

            boolean lastStatement = i == statements.size()-1;

            // some sort of jump
            if (statement.operation.substring(0,1).equals("j")) {
                AssemblyStatement labelStatement = null;
                try {
                    labelStatement = statements.get(labelToIndex.get(statement.operands[0].value()));
                }
                catch (NullPointerException e) {
                    System.out.println(e);
                    System.out.println(statement.toString());
                    System.out.println(statement.operands == null);
                    System.out.println(statement.operands[0] == null);
                    System.out.println(statement.operands[0].value() == null);
                    System.out.println(labelToIndex.containsKey(statement.operands[0].value()));
                }

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
    
    public static DirectedGraph<IRStmt> fromIRFuncDecl(IRFuncDecl irFunc) {
        // The body of the irFunc should be a IRSeq
        IRSeq body = (IRSeq) irFunc.body();
        List<IRStmt> statements = body.stmts();
        DirectedGraph<IRStmt> g = new DirectedGraph<>(irFunc.name());

        // Get all labels
        Map<String, IRLabel> labels = new HashMap<String, IRLabel>();
        for (IRStmt stmt : statements) {
            if (stmt instanceof IRLabel) {
                labels.put(((IRLabel) stmt).name(), (IRLabel) stmt);
            }
        }
        
        // Construct the graph
        for (int i = 0; i < statements.size(); i++) {
            IRStmt statement = statements.get(i);
            if (statement instanceof IRJump) {
                IRName name = (IRName) ((IRJump) statement).target();
                g.addEdge(statement, labels.get(name.name()));
            } else if (statement instanceof IRCJump) {
                IRCJump cjump = (IRCJump) statement;
                g.addEdge(statement, labels.get(cjump.trueLabel()));
                if (cjump.falseLabel() == null) {
                    if (i < statements.size() - 1)
                        g.addEdge(statement, statements.get(i + 1));
                } else
                    g.addEdge(statement, labels.get(cjump.falseLabel()));
            } else if (statement instanceof IRReturn) {
                // Do nothing
            } else {
                if (i < statements.size() - 1)
                    g.addEdge(statement, statements.get(i + 1));
            }
        }
        return g;
    }
}
