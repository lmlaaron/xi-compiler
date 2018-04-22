package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class AvailableExpressionAnalysis extends DataflowAnalysisGenKill<IRStmt, Set<String>> {
    
    private static final String MemoryForm = "_m";
    /*
     * Mapping from expression to the set of variables used
     */
    private Map<String, Set<String>> exprToVar;

    public AvailableExpressionAnalysis(DirectedGraph<IRStmt> cfg) {
        super(cfg);
        exprToVar = new HashMap<String, Set<String>>();
    }

    @Override
    protected Set<String> gen(IRStmt node) {
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRExpr source = ((IRMove) node).source();
            if (source instanceof IRBinOp || source instanceof IRMem) {
                Set<String> gen = new HashSet<String>();
                if (!exprToVar.containsKey(source.toString())) {
                    exprToVar.put(source.toString(), new HashSet<String>());
                    addVar(source);
                }
                gen.add(source.toString());
                return set_difference(gen, kill(node));
            }
        }
        // All other cases: empty
        return new HashSet<String>();
    }
    
    private void addVar(IRExpr expr) {
        if (!exprToVar.containsKey(expr.toString()))
            exprToVar.put(expr.toString(), new HashSet<String>());
        Set<String> value = exprToVar.get(expr.toString());

        if (expr instanceof IRTemp) {
            value.add(((IRTemp) expr).name());
        } else if (expr instanceof IRBinOp) {
            addVar(((IRBinOp) expr).left());
            addVar(((IRBinOp) expr).right());
        } else if (expr instanceof IRMem) {
            value.add(MemoryForm);
        }
    }

    @Override
    protected Set<String> kill(IRStmt node) {
        Set<String> kill = new HashSet<String>();
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRMove moveNode = (IRMove) node;
            IRTemp target = (IRTemp) moveNode.target();
            if (moveNode.source() instanceof IRBinOp || moveNode.source() instanceof IRMem) {
                killExprContainTemp(kill, target);
            } else if (moveNode.source() instanceof IRCall) {
                killExprMem(kill);
            }
        } else if (node instanceof IRMove && 
                ((IRMove) node).target() instanceof IRMem && 
                ((IRMove) node).source() instanceof IRTemp) {
            
        }
        // Impossible case: Procedure call (must be move to a temp)
        // All other cases: empty
        return kill;
    }
    
    private void killExprContainTemp(Set<String> kill, IRTemp t) {
        List<String> toBeRemoved = new ArrayList<String>();
        for (String key : exprToVar.keySet()) {
            if (exprToVar.get(key).contains(t.name())) {
                toBeRemoved.add(key);
                kill.add(key);
            }
        }
        for (String key : toBeRemoved) {
            exprToVar.remove(key);
        }
    }
    
    private void killExprMem(Set<String> kill) {
        List<String> toBeRemoved = new ArrayList<String>();
        for (String key : exprToVar.keySet()) {
            if (exprToVar.get(key).contains(MemoryForm)) {
                toBeRemoved.add(key);
                kill.add(key);
            }
        }
        for (String key : toBeRemoved) {
            exprToVar.remove(key);
        }
    }

    @Override
    protected Set<String> set_union(Collection<Set<String>> information) {
        Set<String> set = new HashSet<>();
        for (Set<String> info : information)
            info.forEach(s -> set.add(s));
        return set;
    }
    
    @Override
    protected Set<String> set_difference(Set<String> a, Set<String> b) {
        Set<String> ret = new HashSet<>(a);
        ret.removeAll(b);
        return ret;
    }

    @Override
    protected DFAnalysisDirection getDirection() {
        return DFAnalysisDirection.Forward;
    }

    @Override
    protected Set<String> meet(Collection<Set<String>> information) {
        return set_intersect(information);
    }
    
    private Set<String> set_intersect(Collection<Set<String>> information) {
        if (information.size() == 0)
            return new HashSet<String>();
        
        List<Set<String>> infoList = new ArrayList<Set<String>>(information);
        Set<String> set = infoList.get(0);
        for (int i = 1; i < infoList.size(); i++) {
            set.retainAll(infoList.get(i));
        }
        return set;
    }
    
    @Override
    protected Set<String> top() {
        return new HashSet<String>();
    }

}
