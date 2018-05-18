package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyUtils;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis extends DataflowAnalysisGenKill<AssemblyStatement, Set<String>> {
    public LiveVariableAnalysis(DirectedGraph<AssemblyStatement> cfg) {super(cfg);}

    @Override
    protected Set<String> kill(AssemblyStatement stmt) {
        return AssemblyUtils.def(stmt);
    }

    @Override
    protected Set<String> gen(AssemblyStatement stmt) {
        return AssemblyUtils.use(stmt);
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
        return DFAnalysisDirection.Backward;
    }

    @Override
    protected Set<String> meet(Collection<Set<String>> information) {
        return set_union(information);
    }

    @Override
    protected Set<String> top() {
        // maximal information === no live variables
        return new HashSet<String>();
    }
}
