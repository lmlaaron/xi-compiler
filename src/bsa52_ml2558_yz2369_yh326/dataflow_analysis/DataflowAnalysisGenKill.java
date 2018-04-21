package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Dataflow Analysis superclass providing a general-purpose implementation of the
 * data transfer function
 *
 * @param <GT>
 * @param <IT>
 */
public abstract class DataflowAnalysisGenKill<GT, IT> extends DataflowAnalysis<GT, IT> {
    public DataflowAnalysisGenKill(DirectedGraph<GT> cfg) {
        super(cfg);
    }

    @Override
    protected IT dataTransferFunction(GT node, IT in) {
        return set_union(gen(node), set_difference(in, kill(node)));
    }

    protected abstract IT kill(GT node);
    protected abstract IT gen(GT node);

    protected IT set_union(IT... information) {
        ArrayList<IT> list = new ArrayList<>(information.length);
        for (int i = 0; i < information.length; i++)
            list.add(information[i]);
        return set_union(list);
    }
    protected abstract IT set_union(Collection<IT> information);
    protected abstract IT set_difference(IT a, IT b);
}
