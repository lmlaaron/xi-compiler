package bsa52_ml2558_yz2369_yh326.util.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Graph {
    private String name;
    private Set<CFGNode> vertices;
    private Map<CFGNode, Set<CFGNode>> edges;
    
    public Graph(String name) {
        this.name = name;
        this.vertices = new HashSet<CFGNode>();
        this.edges = new HashMap<CFGNode, Set<CFGNode>>();
    }
    
    public Graph(String name, Set<CFGNode> vertices) {
        this.name = name;
        this.vertices = vertices;
        this.edges = new HashMap<CFGNode, Set<CFGNode>>();
    }
    
    public Graph(String name, Set<CFGNode> vertices, Map<CFGNode, Set<CFGNode>> edges) {
        this.name = name;
        this.vertices = vertices;
        this.edges = edges;
    }
    
    /**Add {@code vertex} to the graph.
     * @param vertex
     */
    public void addVertex(CFGNode vertex) {
        this.vertices.add(vertex);
        if (!this.edges.containsKey(vertex)) {
            this.edges.put(vertex, new HashSet<CFGNode>());
        }
    }
    
    public void addEdge(CFGNode from, CFGNode to) {
        if (!this.edges.containsKey(from)) {
            this.edges.put(from, new HashSet<CFGNode>());
            this.vertices.add(from);
        }
        this.edges.get(from).add(to);
    }
    
    public boolean removeVertex(CFGNode vertex) {
        return this.vertices.remove(vertex);
    }
    
    public boolean removeEdge(CFGNode from, CFGNode to) {
        if (!this.edges.containsKey(from))
            return false;
        else {
            return this.edges.get(from).remove(to);
        }
    }

    public Set<CFGNode> getVertices() {
        return vertices;
    }

    public Map<CFGNode, Set<CFGNode>> getEdges() {
        return edges;
    }

    public String toDotFormat() {
        String rs = "digraph " + name + "{\n    node [shape=box];\n";
        for (CFGNode vertex : this.vertices) {
            rs += "    " + vertex.id + " " + "[label=\"" + vertex.label.replace("\"", "\\\"") + "\"];\n";
        }
        rs += "\n";
        for (CFGNode from : this.edges.keySet()) {
            for (CFGNode to : this.edges.get(from)) {
                rs += "    " + from.id + " -> " + to.id + ";\n";
            }
        }
        return rs + "}\n";
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
