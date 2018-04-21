package bsa52_ml2558_yz2369_yh326.util.graph;

import java.util.Map;
import java.util.Set;

public interface Graph<T> {
    void addVertex(T vertex);
    void addEdge(T from, T to);
    Map<T, Set<T>> getEdges();
    void removeEdge(T from, T to);
    void removeVertex(T vertex);
    Set<T> getVertices();

}
