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
    
    public Graph(String name) {
        this.name = name;
        this.vertices = new HashSet<CFGNode<T>>();
        this.edges = new HashMap<CFGNode<T>, Set<CFGNode<T>>>();
    }
    
    public Graph(String name, Set<CFGNode<T>> vertices) {
        this.name = name;
        this.vertices = vertices;
        this.edges = new HashMap<CFGNode<T>, Set<CFGNode<T>>>();
    }
    
    public Graph(String name, Set<CFGNode<T>> vertices, Map<CFGNode<T>, Set<CFGNode<T>>> edges) {
        this.name = name;
        this.vertices = vertices;
        this.edges = edges;
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
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new HashSet<CFGNode<T>>());
            this.vertices.add(from);
        }
        this.edges.get(from).add(to);
    }
    
    public boolean removeVertex(CFGNode<T> vertex) {
        return this.vertices.remove(vertex);
    }
    
    public boolean removeEdge(CFGNode<T> from, CFGNode<T> to) {
        if (!this.edges.containsKey(from))
            return false;
        else {
            return this.edges.get(from).remove(to);
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
        HashMap<CFGNode<T>, Set<CFGNode<T>>> newEdges = new HashMap<>();

        for (CFGNode<T> fromNode : getVertices()) {
            for (CFGNode<T> toNode : getEdges().get(fromNode)) {
                if (!newEdges.containsKey(toNode)) {
                    newEdges.put(toNode, new HashSet<>());
                }
                newEdges.get(toNode).add(fromNode);
            }
        }

        this.edges = newEdges;
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
