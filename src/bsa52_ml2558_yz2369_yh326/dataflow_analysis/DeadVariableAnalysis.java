package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
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
            //Set<IRStmt> ret = in;	// Not correct to copy a HashMap like this
            Set<IRStmt> ret = new HashSet<>(in);
            if (node instanceof IRMove) {
                        IRMove move = (IRMove) node;
                        if (move.target() instanceof IRTemp ) {
                        			Iterator<IRStmt> from_ret_iter= ret.iterator();
                                while (from_ret_iter.hasNext()) {
                            		IRStmt from_ret= from_ret_iter.next();
                            			
                            				// notice that for killing a element, we match by value (not reference)
                                        if (from_ret instanceof IRMove && 
                                        		((IRMove) from_ret).target() instanceof IRTemp &&
                                        		((IRTemp) ((IRMove) from_ret).target()).name().equals(((IRTemp) move.target()).name())) {
                                        		
           	                                            	   	from_ret_iter.remove();
                                               
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

    @Override
    protected Set<IRStmt> top() {
        return new HashSet<IRStmt>();
    }
}

