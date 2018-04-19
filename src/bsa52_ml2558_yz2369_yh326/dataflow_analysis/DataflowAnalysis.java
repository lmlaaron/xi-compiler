package bsa52_ml2558_yz2369_yh326.dataflow_analysis;

import bsa52_ml2558_yz2369_yh326.util.graph.Graph;
import bsa52_ml2558_yz2369_yh326.util.graph.Node.CFGNode;

import java.util.*;

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

    /**
     * @return object representing maximal information in this dataflow analysis.
     */
    protected abstract IT top();

    public void worklist() {
        // each vertex has associated data
        Map<CFGNode<GT>, IT> inForNode = new HashMap<>();
        Map<CFGNode<GT>, IT> outForNode = new HashMap<>();

        // all nodes start out with 'top'
        for (CFGNode<GT> node : cfg.getVertices()) {
            inForNode.put(node, top());
            outForNode.put(node, top());
        }

        // all nodes start on worklist
        Stack<CFGNode<GT>> worklist = new Stack<CFGNode<GT>>();
        worklist.addAll(cfg.getVertices());

        while (!worklist.isEmpty()) {
            CFGNode<GT> node = worklist.pop();

            boolean changed = false;

            Collection<IT> outsFromPredecessors = new LinkedList<>();
            cfg.getPredecessors(node).stream().forEach(pred -> outsFromPredecessors.add(outForNode.get(pred)));

            // update in[n]
            IT oldIn = inForNode.get(node);
            IT newIn = in(node.data, outsFromPredecessors);

            if (!newIn.equals(oldIn)) {
                inForNode.put(node, newIn);
                changed = true;
            }

            // update out[n]
            IT oldOut = outForNode.get(node);
            IT newOut = out(node.data, newIn);

            if (!newOut.equals(oldOut)) {
                outForNode.put(node, newOut);
                changed = true;
            }

            // if there were any changes, add all successors to list:
            if (changed) {
                worklist.addAll(cfg.getSuccessors(node));
            }
        }

        // TODO: RETURN inForNode, outForNode in whatever format most convenient (augmented CFG?)
    }
}
