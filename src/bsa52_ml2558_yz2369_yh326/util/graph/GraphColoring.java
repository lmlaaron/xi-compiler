package bsa52_ml2558_yz2369_yh326.util.graph;

import java.util.*;
import java.util.stream.Collectors;

public class GraphColoring<GT, CT> {
    protected Graph<GT> graph;

    public GraphColoring(Graph<GT> graph) {
        this.graph = graph;
    }

    /**
     * @param colors the set of colors which can be used for this graph
     * @return true if all nodes were successfully colored
     */
    public boolean colorBasic(Collection<CT> colors, Map<GT, CT> colorings) {
        // accumulate in-degree counts
        Map<GT, Integer> inDegree = getInDegree();

        int k = colors.size();

        // kempe's heuristic algorithm:
        // we don't want to color nodes that are precolored, so remove them now!
        HashSet<GT> currentNodes = new HashSet<GT>(graph.getVertices().stream().filter(
                v -> !colorings.containsKey(v)
        ).collect(Collectors.toList()));

        LinkedList<GT> toColor = new LinkedList<>();
        // simplification phase: remove nodes until graph is empty
        while (!currentNodes.isEmpty()) {
            toColor.addAll(simplify(k, currentNodes, inDegree));
        }

        // coloring phase: add nodes back in reverse order of removal
        while (!toColor.isEmpty()) {
            GT node = toColor.removeLast();
            colorings.put(node, colorNode(node, colorings, colors));
        }

        return graph.getVertices().stream().allMatch(v -> colorings.containsKey(v));
    }

    protected CT colorNode(GT node, Map<GT, CT> colorings, Collection<CT> colors) {
        Set<GT> coloredPredecessors = graph.getPredecessors(node).stream().filter(
                pred -> colorings.containsKey(pred)
        ).collect(Collectors.toSet());

        Set<CT> takenColors = coloredPredecessors.stream().map(
                p -> colorings.get(p)
        ).collect(Collectors.toSet());

        // if this throws an error, all colors were taken, so there's an implementation problem
        return colors.stream().filter(c -> !takenColors.contains(c)).findFirst().get();
    }

    protected Map<GT, Integer> getInDegree() {
        HashMap<GT, Integer> inDegree = new HashMap<>();

        for (GT v : graph.getVertices())
            inDegree.put(v, 0);
        for (GT from : graph.getEdges().keySet()) {
            for (GT to : graph.getEdges().get(from)) {
                inDegree.put(to, inDegree.get(to)+1);
            }
        }

        return inDegree;
    }

    protected List<GT> simplify(int k, HashSet<GT> currentNodes, Map<GT, Integer> inDegree) {
        LinkedList<GT> removed = new LinkedList<GT>();

        // trivially removable nodes have an in degree less than or equal to k
        List<GT> removable = currentNodes.stream().filter(
                node ->inDegree.get(node) < k
        ).collect(Collectors.toList());


        if (removable.isEmpty()) {
            // pick a node and remove it - it won't be colored
            currentNodes.iterator().remove();
        }
        else {
            for (GT node : removable) {
                // simulate removing the node from the graph
                // by decreasing in-degrees appropriately
                for (GT adj : graph.getSuccessors(node)) {
                    inDegree.put(adj, inDegree.get(adj)-1);
                }
                removed.add(node);
            }
            currentNodes.removeAll(removable);
        }

        return removed;
    }
}
