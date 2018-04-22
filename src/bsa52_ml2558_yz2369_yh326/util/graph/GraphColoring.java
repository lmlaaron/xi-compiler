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
     * @return a list of the nodes that couldn't be colored
     */
    public List<GT> colorBasic(Collection<CT> colors, Map<GT, CT> colorings) {
        return colorRestricted(colors, colorings, new HashSet<CT>());
    }

    /**
     * Operates similarly to colorBasic, but imposes the restriction that
     * some subset of nodes must be colored
     *
     * @param mustColor
     *
     * @throws RuntimeException if the restriction couldn't be satisfied
     */
    public List<GT> colorRestricted(Collection<CT> colors, Map<GT, CT> colorings, Set<CT> mustColor) {
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
            toColor.addAll(simplify(k, currentNodes, inDegree, mustColor));
        }

        // coloring phase: add nodes back in reverse order of removal
        while (!toColor.isEmpty()) {
            GT node = toColor.removeLast();
            colorings.put(node, colorNode(node, colorings, colors));
        }

        return graph.getVertices().stream().filter(
                v -> !colorings.containsKey(v)
        ).collect(Collectors.toList());
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

    protected List<GT> simplify(int k, HashSet<GT> currentNodes, Map<GT, Integer> inDegree, Set<CT> mustColor) {
        LinkedList<GT> removed = new LinkedList<GT>();

        // trivially removable nodes have an in degree less than or equal to k
        List<GT> removable = currentNodes.stream().filter(
                node ->inDegree.get(node) < k
        ).collect(Collectors.toList());


        if (removable.isEmpty()) {
            // pick a node and remove it - it won't be colored
            GT uncolored = null;

            Iterator<GT> it = currentNodes.iterator();
            while (it.hasNext() && uncolored == null) {
                GT node = it.next();
                if (!mustColor.contains(node)) {
                    uncolored = node;
                    it.remove();
                }
            }

            if (uncolored == null) {
                // TODO: turn into some kind of checked exception
                throw new RuntimeException("Couldn't satisfy restrictions!");
            }

            for (GT adj : graph.getSuccessors(uncolored))
                inDegree.put(adj, inDegree.get(adj)-1);
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
