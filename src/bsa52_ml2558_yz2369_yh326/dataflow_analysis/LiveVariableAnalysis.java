package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LiveVariableAnalysis extends DataflowAnalysisGenKill<AssemblyStatement, Set<String>> {
    public LiveVariableAnalysis(DirectedGraph<AssemblyStatement> cfg) {super(cfg);}

    @Override
    protected Set<String> kill(AssemblyStatement stmt) {
        // equivalent to 'def' from lecture materials

        // statements of the form MOV TEMP, *SOMETHING*
        // define TEMP

        HashSet<String> set = new HashSet<String>();
        if (stmt.operands.length != 2) return set;
        else if (stmt.operation.equals("mov")) {
            AssemblyOperand possiblyTemp = stmt.operands[0];

            possiblyTemp.ResolveType();
            if (possiblyTemp.type.equals(AssemblyOperand.OperandType.TEMP)) {
                set.add(possiblyTemp.value());
            }
        }
        return set;
    }

    @Override
    protected Set<String> gen(AssemblyStatement stmt) {
        // equivalent to 'use' from lecture materials

        // it is correct to say a temp is used in every context in which it appears, except where it is killed

        HashSet<String> set = new HashSet<String>();

        Arrays.stream(stmt.operands).forEach(op -> op.ResolveType());

        if (stmt.operation.equals("mov")) {
            AssemblyOperand dest = stmt.operands[0];
            // don't want to say a temp is used if it's being assigned to
            if (!dest.type.equals(AssemblyOperand.OperandType.TEMP)) {
                set.addAll(dest.getTemps());
            }
            set.addAll(stmt.operands[1].getTemps());
        }
        // aside from mov, anywhere a temp is used everywhere it appears
        else {
            Arrays.stream(stmt.operands).forEach(op -> set.addAll(op.getTemps()));
        }

        return set;
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
