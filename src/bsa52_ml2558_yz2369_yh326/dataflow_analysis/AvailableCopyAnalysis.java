package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;


// brief for available copy analysis
//       n                                 *                use(n)                     *              def(n)
//    x = y                              *                {x=y}                       *   x=z, z=x for all z
//    x = e  where e!=z          *               nothing                   *   x=z, z=x
//     if e                                 *               nothing                  * nothing
//     [e_1]= e_2                     *               nothing                 * nothing
//     START                            *              nothing                  * {all nodes}
//     EXIT                                *              nothing or RV       *     nothing

// the gen and kill set should be assignment x=y, however, sometimes z cannot be
// decided only on the node n itself (e.g., z=x for all z, z cannot decided), in order to
// resolve this, we use string to represent values of DFA, and use the string should 
// be fall into the following categories
// 1. IRMOVE(IRTEMP(x), IRTEMP(y))
// 2. IRTEMP(x)       // this represents that all x=z, z=x be removed
// 3. ALL 					// this represents that all nodes be removed
// Special care needs to be taken in set_union, set_difference, data_transferFucntion
// theoratically, after set_difference, the abormal IRTEMP(x) and ALL should be gone
public class AvailableCopyAnalysis extends DataflowAnalysisGenKill<IRStmt, Set<String>> {
    
    //private static final String MemoryForm = "_m";
    /*
     * Mapping from expression to the set of variables used
     */
    //private Map<String, Set<String>> exprToVar;

    public AvailableCopyAnalysis(DirectedGraph<IRStmt> cfg) {
        super(cfg);
        
        //exprToVar = new HashMap<String, Set<String>>();
    }

    @Override
    public Set<String> gen(IRStmt node) {
    	
        Set<String> set = new HashSet<String>();
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRExpr source = ((IRMove) node).source();
            if (source instanceof IRTemp) {
            		set.add(node.toString());
            }
        } else if (node instanceof IRReturn) {
        		IRReturn irret = (IRReturn)node;
        		// TODO need to think about what RV is here
        		assert false;
        }
        return set;
    }
    
    @Override
    protected Set<String> kill(IRStmt node) {
    		// for def need to know what is z in x=z and z=x, use   pointer instead
        Set<String> set = new HashSet<String>();
        if (node instanceof IRMove && ((IRMove) node).target() instanceof IRTemp) {
            IRExpr source = ((IRMove) node).source();
            set.add(source.toString());
         // TODO handele START separately
        // } else if (node instanceof IRStart) {
        //		assert false;
        } 
        return set;
    }

    @Override
    protected Set<String> dataTransferFunction(IRStmt node, Set<String> in) {
        return set_union(gen(node), set_difference(in, kill(node)));
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
    		// if b contains "ALL", return empty
    		for ( String s: b) {
    			if ( s.equals("ALL")) {
    				Set<String> ret = new HashSet<String>();
    				return ret;
    			}
    		}
    	
    		// if b contains anything start with IRTemp, remove from a any thing that contains the same temp
    		Set<String> ret = new HashSet<>(a);
    		for ( String from_b: b ) {
    			if (from_b.startsWith("IRTemp")) {
    				String tempName = from_b.substring("IRTemp".length(), from_b.length()-1);
    				for ( String from_ret: ret) {
    					if (from_ret.contains(tempName)) {
    						assert ret.remove(from_ret);
    					}
    				}
    			}
    		}

    		// normal set difference
    		for (String from_b: b) {
    			for (String from_ret:ret) {
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
    protected Set<String> meet(Collection<Set<String>> information) {
        return set_intersect(information);
    }
    
    private Set<String> set_intersect(Collection<Set<String>> information) {
        if (information.size() == 0)
            return new HashSet<String>();
        
        List<Set<String>> infoList = new ArrayList<>(information);
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



