package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class AvailableExpressionAnalysis extends DataflowAnalysisGenKill<IRStmt, Set<Tuple<String, IRStmt>>> {
    
    private static final String MemoryForm = "_m";
    /*
     * Mapping from expression to the set of variables used
     */
    private Map<String, Set<String>> exprToVar;
    private Map<String, IRStmt> exprToDefStmt;
    private List<String> toBeRemoved;

    public AvailableExpressionAnalysis(DirectedGraph<IRStmt> cfg) {
        super(cfg);
        exprToVar = new HashMap<String, Set<String>>();
        exprToDefStmt = new HashMap<String, IRStmt>();
        toBeRemoved = new ArrayList<String>();
    }

    @Override
    protected Set<Tuple<String, IRStmt>> gen(IRStmt node) {
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRExpr source = ((IRMove) node).source();
            if (source instanceof IRBinOp || source instanceof IRMem || source instanceof IRConst) {
                Set<Tuple<String, IRStmt>> gen = new HashSet<Tuple<String, IRStmt>>();
                gen.add(new Tuple<>(source.toString(), node));
                return set_difference(gen, kill(node));
            }
        }
        // All other cases: empty
        return new HashSet<Tuple<String, IRStmt>>();
    }
    
    private void addVar(String exprString, IRExpr expr) {
        if (expr instanceof IRConst) return;
        
        if (!exprToVar.containsKey(exprString))
            exprToVar.put(exprString, new HashSet<String>());
        Set<String> value = exprToVar.get(exprString);
        if (expr instanceof IRTemp) {
            value.add(((IRTemp) expr).name());
        } else if (expr instanceof IRBinOp) {
            addVar(exprString, ((IRBinOp) expr).left());
            addVar(exprString, ((IRBinOp) expr).right());
        } else if (expr instanceof IRMem) {
            value.add(MemoryForm);
        }
    }

    @Override
    protected Set<Tuple<String, IRStmt>> kill(IRStmt node) {
        System.out.println("killing: "+node);
        Set<Tuple<String, IRStmt>> kill = new HashSet<Tuple<String, IRStmt>>();
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRMove moveNode = (IRMove) node;
            IRTemp target = (IRTemp) moveNode.target();
            if (moveNode.source() instanceof IRBinOp || 
                    moveNode.source() instanceof IRMem || 
                    moveNode.source() instanceof IRConst) {
                System.out.println("!!!");
                killExprContainTemp(kill, target);
            } else if (moveNode.source() instanceof IRCall) {
                System.out.println("???");
                killExprMem(kill);
            }
        } else if (node instanceof IRMove && 
                ((IRMove) node).target() instanceof IRMem && 
                ((IRMove) node).source() instanceof IRTemp) {
            
        }
        // Impossible case: Procedure call (must be move to a temp)
        // All other cases: empty
        System.out.println("killed: "+kill);
        return kill;
    }
    
    private void killExprContainTemp(Set<Tuple<String, IRStmt>> kill, IRTemp t) {
        System.out.println("exprToVar: " + exprToVar);
        for (String key : exprToVar.keySet()) {
            if (exprToVar.get(key).contains(t.name())) {
                toBeRemoved.add(key);
                kill.add(new Tuple<>(key, exprToDefStmt.get(key)));
            }
        }
    }
    
    private void killExprMem(Set<Tuple<String, IRStmt>> kill) {
        for (String key : exprToVar.keySet()) {
            if (exprToVar.get(key).contains(MemoryForm)) {
                toBeRemoved.add(key);
                kill.add(new Tuple<>(key, exprToDefStmt.get(key)));
            }
        }
    }
    
    @Override
    protected Set<Tuple<String, IRStmt>> dataTransferFunction(IRStmt node, Set<Tuple<String, IRStmt>> in) {
        Set<Tuple<String, IRStmt>> rs = set_union(gen(node), set_difference(in, kill(node)));
        for (String key : toBeRemoved) {
            exprToVar.remove(key);
            exprToDefStmt.remove(key);
        }
        toBeRemoved.clear();
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRExpr source = ((IRMove) node).source();
            if (source instanceof IRBinOp || source instanceof IRMem || source instanceof IRConst) {
                exprToDefStmt.put(source.toString(), node);
                if (!exprToVar.containsKey(source.toString())) {
                    exprToVar.put(source.toString(), new HashSet<String>());
                    addVar(source.toString(), source);
                }
            }
        }
        return rs;
    }

    @Override
    protected Set<Tuple<String, IRStmt>> set_union(Collection<Set<Tuple<String, IRStmt>>> information) {
        Set<Tuple<String, IRStmt>> set = new HashSet<>();
        for (Set<Tuple<String, IRStmt>> info : information)
            info.forEach(s -> set.add(s));
        return set;
    }
    
    @Override
    protected Set<Tuple<String, IRStmt>> set_difference(Set<Tuple<String, IRStmt>> a, Set<Tuple<String, IRStmt>> b) {
        Set<Tuple<String, IRStmt>> ret = new HashSet<>(a);
        Set<Tuple<String, IRStmt>> remove = new HashSet<>();
        
        for (Tuple<String, IRStmt> aitem : ret) {
            for (Tuple<String, IRStmt> bitem: b) {
                if (aitem.equals(bitem)) {
                    remove.add(aitem);
                }
            }
        }
        for (Tuple<String, IRStmt> item : remove) {
            ret.remove(item);
        }
        
        System.out.println("DIFFERENCE !!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("a"+a);
        System.out.println("b"+b);
        System.out.println("r"+ret);
        return ret;
    }

    @Override
    protected DFAnalysisDirection getDirection() {
        return DFAnalysisDirection.Forward;
    }

    @Override
    protected Set<Tuple<String, IRStmt>> meet(Collection<Set<Tuple<String, IRStmt>>> information) {
        return set_intersect(information);
    }
    
    private Set<Tuple<String, IRStmt>> set_intersect(Collection<Set<Tuple<String, IRStmt>>> information) {
        if (information.size() == 0)
            return new HashSet<Tuple<String, IRStmt>>();
        
        List<Set<Tuple<String, IRStmt>>> infoList = new ArrayList<>(information);
        Set<Tuple<String, IRStmt>> set = infoList.get(0);
        for (int i = 1; i < infoList.size(); i++) {
            set.retainAll(infoList.get(i));
        }
        return set;
    }
    
    @Override
    protected Set<Tuple<String, IRStmt>> top() {
        return new HashSet<Tuple<String, IRStmt>>();
    }

}
