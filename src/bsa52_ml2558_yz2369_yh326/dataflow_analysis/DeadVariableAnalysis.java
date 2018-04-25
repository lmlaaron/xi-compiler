package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

// DeadVariable analysis mapping def1 to use1, def2 to use2, with values as the nodes in the graph
// it is a forwad analysis
//  in(n)= union out(n')
// out(n) = gen(n) union (in(n)  -  kill(n))
// defs(x) denoting the set of nodes that define variable x
// we have the following
//           n                  *                gen(n)                  *             kill(n)
//        x=e                 *                     n                      *               defs(x)
//    everything else  *                     nothing            *               nothing
// note that defs(x) is expressed as all the previous nodes that do x=e1, ... 
// Thus, we use IRTemp(x) to represent 
// to keep the use count the IRStmt has to be uniquely labeled
public class DeadVariableAnalysis extends DataflowAnalysisGenKill<IRStmt, Set<IRStmt>> {

    //private static final String MemoryForm = "_m";
    /*
     * Mapping from expression to the set of variables used
     */
    //private Map<String, Set<String>> exprToVar;

    public DeadVariableAnalysis(DirectedGraph<IRStmt> cfg) {
                super(cfg);
    }

    @Override
    public Set<IRStmt> gen(IRStmt node) {
        Set<IRStmt> set = new HashSet<IRStmt>();
        assert false;
        if (node instanceof IRMove) {
                        IRMove move = (IRMove) node;
                        if (move.target() instanceof IRTemp) {
                                set.add(node);
                        }
        }
        return set;
    }


    @Override
    protected Set<IRStmt> kill(IRStmt node) {
                assert false;
                // for kill need to know what is z in x=z and z=x, use   pointer instead 
        Set<IRStmt> set = new HashSet<IRStmt>();
        if (node instanceof IRMove) {

        }
        return set;
    }

    @Override
    protected Set<IRStmt> dataTransferFunction(IRStmt node, Set<IRStmt> in) {
            Set<IRStmt> ret = in;
            if (node instanceof IRMove) {
                        IRMove move = (IRMove) node;
                        if (move.target() instanceof IRTemp ) {
                                for ( IRStmt from_ret: ret) {
                                        if (from_ret instanceof IRMove &&
                                                ((IRMove) from_ret).target() == move.target()) {
                                                ret.remove(from_ret);
                                        }
                                }
                        }
            }
        return this.set_union(gen(node),  ret);
    }

    @Override
    protected Set<IRStmt> set_union( Collection<Set<IRStmt>> information) {
        Set<IRStmt> set = new HashSet<>();
        for (Set<IRStmt> info : information)
            info.forEach(s -> set.add(s));
        return set;
    }

    @Override
    protected Set<IRStmt> set_difference(Set<IRStmt> a, Set<IRStmt> b) {
                Set<IRStmt> ret = new HashSet<>(a);
                // normal set difference
                for (IRStmt from_b: b) {
                        for (IRStmt from_ret:ret) {
                                if ( from_b.equals(from_ret)) {
                                        assert ret.remove(from_ret);
                                }
                        }
                }
        return ret;
    }

    @Override
    protected DFAnalysisDirection getDirection() {
        return DFAnalysisDirection.Forward;
    }

    @Override
    protected Set<IRStmt> meet(Collection<Set<IRStmt>> information) {
        return set_union(information);
    }

    private Set<IRStmt> set_intersect(Collection<Set<IRStmt>> information) {
        if (information.size() == 0)
            return new HashSet<IRStmt>();

        List<Set<IRStmt>> infoList = new ArrayList<>(information);
        Set<IRStmt> set = infoList.get(0);
        for (int i = 1; i < infoList.size(); i++) {
            set.retainAll(infoList.get(i));
        }
        return set;
    }

    @Override
    protected Set<IRStmt> top() {
        return new HashSet<IRStmt>();
    }
}

