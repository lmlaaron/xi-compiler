package bsa52_ml2558_yz2369_yh326.util.graph;

import java.util.*;

public class UndirectedGraph<T> implements Graph<T> {
    protected Map<T, Set<T>> edges;
    protected Set<T> vertices;

    public UndirectedGraph() {
        edges = new HashMap<>();
        vertices = new HashSet<>();
    }

    public Set<T> getSuccessors(T node) {
        if (edges.containsKey(node)) {
            return edges.get(node);
        }
        else {
            return new HashSet<>();
        }
    }

    public Set<T> getPredecessors(T node) {
        return getSuccessors(node);
    }


    @Override
    public void addVertex(T vertex) {
        vertices.add(vertex);
    }

    @Override
    public void addEdge(T from, T to) {
        addVertex(from);
        addVertex(to);

        addDirectedEdge(from, to);
        addDirectedEdge(to, from);
    }

    protected void addDirectedEdge(T from, T to) {
        if (!edges.containsKey(from)) {
            edges.put(from, new HashSet<T>());
        }
        edges.get(from).add(to);
    }

    protected void removeDirectedEdge(T from, T to) {
        if (edges.containsKey(from)) {
            edges.get(from).remove(to);
        }
    }

    @Override
    public Map<T, Set<T>> getEdges() {
        return edges;
    }

    @Override
    public void removeEdge(T from, T to) {
        removeDirectedEdge(from, to);
        removeDirectedEdge(to, from);
    }

    @Override
    public void removeVertex(T vertex) {
        if (edges.containsKey(vertex)) {
            LinkedList<T> adjs = new LinkedList<T>(edges.get(vertex));
            // inefficient, but other methods don't seem to work
            for (Set<T> set : edges.values())
                set.remove(vertex);
            edges.remove(vertex);
        }
        vertices.remove(vertex);
    }

    @Override
    public Set<T> getVertices() {
        return vertices;
    }
}
