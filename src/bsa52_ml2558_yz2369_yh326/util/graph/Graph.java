package bsa52_ml2558_yz2369_yh326.util.graph;

import bsa52_ml2558_yz2369_yh326.util.graph.Node.CFGNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph<T> {
    private String name;
    private Set<CFGNode<T>> vertices;
    private Map<CFGNode<T>, Set<CFGNode<T>>> edges;
    private Map<CFGNode<T>, Set<CFGNode<T>>> reverseEdges;
    
    public Graph(String name) {
        this.name = name;
        this.vertices = new HashSet<CFGNode<T>>();
        this.edges = new HashMap<CFGNode<T>, Set<CFGNode<T>>>();
        this.reverseEdges = new HashMap<>();
    }
    
    public Graph(String name, Set<CFGNode<T>> vertices) {
        this.name = name;
        this.vertices = vertices;
        this.edges = new HashMap<>();
        this.reverseEdges = new HashMap<>();
    }
    
    public Graph(String name, Set<CFGNode<T>> vertices, Map<CFGNode<T>, Set<CFGNode<T>>> edges) {
        this.name = name;
        this.vertices = vertices;

        this.edges = new HashMap<>();
        this.reverseEdges = new HashMap<>();

        for (CFGNode from : edges.keySet()) {
            for (CFGNode to : edges.get(from)) {
                addEdge(from, to);
            }
        }
    }
    
    /**Add {@code vertex} to the graph.
     * @param vertex
     */
    public void addVertex(CFGNode<T> vertex) {
        this.vertices.add(vertex);
        if (!this.edges.containsKey(vertex)) {
            this.edges.put(vertex, new HashSet<CFGNode<T>>());
        }
    }
    
    public void addEdge(CFGNode<T> from, CFGNode<T> to) {
        this.vertices.add(from);
        this.vertices.add(to);

        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new HashSet<CFGNode<T>>());
        }
        if (!this.reverseEdges.containsKey(to)) {
            this.reverseEdges.put(to, new HashSet<>());
        }

        this.edges.get(from).add(to);
        this.reverseEdges.get(to).add(from);
    }

    public Set<CFGNode<T>> getPredecessors(CFGNode<T> node) {
        if (reverseEdges.containsKey(node))
            return reverseEdges.get(node);
        else
            return new HashSet<>();
    }

    public Set<CFGNode<T>> getSuccessors(CFGNode<T> node) {
        if (edges.containsKey(node)) {
            return edges.get(node);
        }
        else
            return new HashSet<>();
    }
    
    public boolean removeVertex(CFGNode<T> vertex) {
        return this.vertices.remove(vertex);
    }
    
    public void removeEdge(CFGNode<T> from, CFGNode<T> to) {
        if (!this.edges.containsKey(from) || !this.reverseEdges.containsKey(to))
            return;
        else {
            this.edges.get(from).remove(to);
            this.reverseEdges.get(to).remove(from);
        }
    }

    public Set<CFGNode<T>> getVertices() {
        return vertices;
    }

    public Map<CFGNode<T>, Set<CFGNode<T>>> getEdges() {
        return edges;
    }

    public String toDotFormat() {
        String rs = "digraph " + name + "{\n    node [shape=box];\n";
        for (CFGNode vertex : this.vertices) {
            rs += "    " + vertex.id + " " + "[label=\"" + vertex.data.toString().replace("\"", "\\\"") + "\"];\n";
        }
        rs += "\n";
        for (CFGNode from : this.edges.keySet()) {
            for (CFGNode to : this.edges.get(from)) {
                rs += "    " + from.id + " -> " + to.id + ";\n";
            }
        }
        return rs + "}\n";
    }

    @Override
    public Graph<T> clone() {
        return new Graph<T>(name, new HashSet<>(getVertices()), new HashMap<>(getEdges()));
    }

    public void reverseEdges() {
        Map<CFGNode<T>, Set<CFGNode<T>>> placeholder = edges;
        edges = reverseEdges;
        reverseEdges = edges;
    }

    public static void main(String[] argv) {
        Graph g = new Graph("testFunction");
        CFGNode n1 = new CFGNode("a = 1"), n2 = new CFGNode("if a < 3"), 
                n3 = new CFGNode("b = 4"), n4 = new CFGNode("b = 5"),
                n5 = new CFGNode("c = b");
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
