package bsa52_ml2558_yz2369_yh326.util.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DirectedGraph<T> implements Graph<T> {
    private String name;
    private Set<T> vertices;
    private Map<T, Set<T>> edges;
    private Map<T, Set<T>> reverseEdges;
    
    public DirectedGraph(String name) {
        this.name = variableName(name);
        this.vertices = new HashSet<T>();
        this.edges = new HashMap<T, Set<T>>();
        this.reverseEdges = new HashMap<>();
    }
    
    public DirectedGraph(String name, Set<T> vertices) {
        this.name = variableName(name);
        this.vertices = vertices;
        this.edges = new HashMap<>();
        this.reverseEdges = new HashMap<>();
    }
    
    public DirectedGraph(String name, Set<T> vertices, Map<T, Set<T>> edges) {
        this.name = variableName(name);
        this.vertices = vertices;

        this.edges = new HashMap<>();
        this.reverseEdges = new HashMap<>();

        for (T from : edges.keySet()) {
            for (T to : edges.get(from)) {
                addEdge(from, to);
            }
        }
    }
    
    private String variableName(String name) {
        String rs = "";
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if ('A' <= ch && ch <= 'Z' || 'a' <= ch && ch <= 'z'
                    || '0' <= ch && ch <= '9' || ch == '_')
                rs += ch;
            else rs += "_";
        }
        return rs;
    }
    
    /**Add {@code vertex} to the graph.
     * @param vertex
     */
    public void addVertex(T vertex) {
        this.vertices.add(vertex);
        if (!this.edges.containsKey(vertex)) {
            this.edges.put(vertex, new HashSet<T>());
        }
    }
    
    public void addEdge(T from, T to) {
        this.vertices.add(from);
        this.vertices.add(to);

        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new HashSet<T>());
        }
        if (!this.reverseEdges.containsKey(to)) {
            this.reverseEdges.put(to, new HashSet<>());
        }

        this.edges.get(from).add(to);
        this.reverseEdges.get(to).add(from);
    }

    public Set<T> getPredecessors(T node) {
        if (reverseEdges.containsKey(node))
            return reverseEdges.get(node);
        else
            return new HashSet<>();
    }

    public Set<T> getSuccessors(T node) {
        if (edges.containsKey(node)) {
            return edges.get(node);
        }
        else
            return new HashSet<>();
    }
    
    public void removeVertex(T vertex) {
        this.vertices.remove(vertex);
        if (vertices.contains(vertex))
            vertices.remove(vertex);
        if (reverseEdges.containsKey(vertex)) {
            LinkedList<T> froms  = new LinkedList(reverseEdges.get(vertex));
            for (T from : froms)
                removeEdge(from, vertex);
        }
        if (edges.containsKey(vertex))
            edges.remove(vertex);
    }

    public void removeEdge(T from, T to) {
        if (!this.edges.containsKey(from) || !this.reverseEdges.containsKey(to))
            return;
        else {
            this.edges.get(from).remove(to);
            this.reverseEdges.get(to).remove(from);
        }
    }

    public Set<T> getVertices() {
        return vertices;
    }

    public Map<T, Set<T>> getEdges() {
        return edges;
    }

    public String toDotFormat() {
        // assign an id to each node
        int id = 0;
        HashMap<T, Integer> ids = new HashMap<T, Integer>();
        for (T vert : vertices)
            ids.put(vert, id++);


        String rs = "digraph " + name + "{\n    node [shape=box];\n";
        for (T vertex : this.vertices) {
            String label = vertex.toString().trim().replace("\"", "\\\"");
            rs += "    " + ids.get(vertex) + " " + "[label=\"" + label + "\"];\n";
        }
        rs += "\n";
        for (T from : this.edges.keySet()) {
            for (T to : this.edges.get(from)) {
                rs += "    " + ids.get(from) + " -> " + ids.get(to) + ";\n";
            }
        }
        return rs + "}\n";
    }

    @Override
    public DirectedGraph<T> clone() {
        return new DirectedGraph<T>(name, new HashSet<>(getVertices()), new HashMap<>(getEdges()));
    }

    public void reverseEdges() {
        Map<T, Set<T>> placeholder = edges;
        edges = reverseEdges;
        reverseEdges = edges;
    }

    public static void main(String[] argv) {
        DirectedGraph<String> g = new DirectedGraph<String>("testFunction");
        String n1 = "a = 1",
                n2 = "if a < 3",
                n3 = "b = 4",
                n4 = "b = 5",
                n5 = "c = b";
        g.addVertex(n1);
        g.addVertex(n2);
        g.addVertex(n3);
        g.addVertex(n4);
        g.addVertex(n5);
        g.addEdge(n1, n2);
        g.addEdge(n2, n3);
        g.addEdge(n2, n4);
        g.addEdge(n3, n5);
        g.addEdge(n4, n5);
        try {
            FileWriter writer = new FileWriter("testFile_" + g.name + "_initial.dot");
            writer.write(g.toDotFormat());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
