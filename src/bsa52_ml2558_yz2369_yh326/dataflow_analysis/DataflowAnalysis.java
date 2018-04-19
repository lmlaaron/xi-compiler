package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.util.graph.Graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Parent class encapsulating common functionalities for dataflow analysis
 *
 * @param <GT> [graph type] The type of CFG elements this analysis will be performed upon
 * @param <IT> [information type] The type which encapsulates the information propagated using in, out, etc.
 */
public abstract class DataflowAnalysis<GT, IT> {
    /**
     * Control Flow Graph for this instance to perform analysis on
     */
    protected Graph<GT> cfg;

    public DataflowAnalysis(Graph<GT> cfg) {
        this.cfg = cfg;

        // By reversing the direction of the graph, we can
        // write dataflow analysis for just the forward direction
        if (getDirection().equals(DFAnalysisDirection.Backward)) {
            this.cfg = cfg.clone();
            this.cfg.reverseEdges();
        }
    }

    /**
     * @return The direction of the flow of information. In forward analysis, information is transferred
     *          through the directed edges of the cfg. In reverse, information is transferred in the
     *          opposite direction through those edges
     */
    protected abstract DFAnalysisDirection getDirection();

    /**
     * @param outs out[n'] values for each predecessor n' of n
     * @return the conservatively combined information
     */
    protected IT in(GT node, Collection<IT> outs) {
        return meet(outs);
    }

    /**
     * @param node the current node transforming information from
     * @param in
     * @return
     */
    protected IT out(GT node, IT in) {
        return dataTransferFunction(node, in);
    }

    /**
     * The meet operation conservatively combines information from multiple sources
     * @param information the IT instances, usually provided by out[n'] from each of a node's predecessors
     * @return the conservatively combined information
     */
    protected abstract IT meet(Collection<IT> information);

    /**
     * Referred to as Fn in class, this function transforms
     * in[n] into out[n] for forward analysis
     */
    protected abstract IT dataTransferFunction(GT node, IT in);
}
