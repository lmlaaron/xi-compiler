package bsa52_ml2558_yz2369_yh326.util.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

public class DirectedGraph<T> implements Graph<T> {
    public String name;
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

    public Set<T> getAdj(T node) {
        if (edges.containsKey(node)) {
            return edges.get(node);
        }
        else {
            return new HashSet<>();
        }
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
        vertices.remove(vertex);
        
        Set<T> tos = edges.get(vertex);
        if (tos != null) {
            tos = new HashSet<>(tos);
            tos.forEach(to -> removeEdge(vertex, to));
        }
        edges.remove(vertex);
        
        Set<T> froms = reverseEdges.get(vertex);
        if (froms != null) {
            froms = new HashSet<>(froms);
            froms.forEach(from -> removeEdge(from, vertex));
        }
        reverseEdges.remove(vertex);
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
    
    public static String GenerateDotString(DirectedGraph<IRStmt> g) {
        DirectedGraph<IRStmt> minimized = MinimizeGraph(g);
        HashMap<IRStmt, Integer> ids = new HashMap<IRStmt, Integer>();
        int id = 0;
        for (IRStmt v : minimized.vertices) ids.put(v, id++);
        String rs = "digraph " + g.name + "{\n    node [shape=box];\n";
        for (IRStmt vertex : minimized.vertices) {
            String label = "";
            if (vertex instanceof IRSeq) {
                for (IRStmt s : ((IRSeq) vertex).stmts()) label += s.toString().trim() + "\\l";
            } else {
                label += vertex.toString();
            }
            label = label.trim().replace("\"", "\\\"");
            rs += "    " + ids.get(vertex) + " " + "[label=\"" + label + "\"];\n";
        }
        rs += "\n";
        for (IRStmt from : minimized.getEdges().keySet()) {
            for (IRStmt to : minimized.getEdges().get(from)) {
                rs += "    " + ids.get(from) + " -> " + ids.get(to) + ";\n";
            }
        }
        return rs + "}\n";
    }
    
    public static DirectedGraph<IRStmt> MinimizeGraph(DirectedGraph<IRStmt> g) {
        DirectedGraph<IRStmt> minimized = new DirectedGraph<>(g.name);
        Stack<IRStmt> vertices = new Stack<>();
        g.vertices.forEach(v -> {
            vertices.push(v);
            Set<IRStmt> next = g.edges.get(v);
            if (next != null) next.forEach(e -> minimized.addEdge(v, e));
        });
        while (!vertices.isEmpty()) {
            IRStmt vert = vertices.pop();
            IRStmt next = CheckSingleAndGetChild(minimized, vert);
            if (next != null) {
                vertices.remove(next);
                IRSeq newVert = combineStmt(vert, next);
                Set<IRStmt> nextNext = minimized.edges.get(next);
                if (nextNext != null) nextNext.forEach(v -> minimized.addEdge(newVert, v));
                Set<IRStmt> prev = minimized.reverseEdges.get(vert);
                if (prev != null) prev.forEach(v -> minimized.addEdge(v, newVert));
                minimized.removeVertex(vert);
                minimized.removeVertex(next);
                vertices.add(newVert);
            }
        }
        return minimized;
    }
    
    public static IRStmt CheckSingleAndGetChild(DirectedGraph<IRStmt> minimized, IRStmt vert) {
        Set<IRStmt> succ = minimized.edges.get(vert);
        if (succ != null && succ.size() == 1) {
            IRStmt next = (new ArrayList<>(succ)).get(0);
            Set<IRStmt> pred = minimized.reverseEdges.get(next);
            if (pred != null && pred.size() == 1) {
                return next;
            }
        }
        return null;
    }
    
    public static IRSeq combineStmt(IRStmt vert, IRStmt next) {
        List<IRStmt> newVertList = new ArrayList<>();
        if (vert instanceof IRSeq)
            newVertList.addAll(((IRSeq) vert).stmts());
        else
            newVertList.add(vert);
        if (next instanceof IRSeq)
            newVertList.addAll(((IRSeq) next).stmts());
        else
            newVertList.add(next);
        return new IRSeq(newVertList);
    }

    @Override
    public DirectedGraph<T> clone() {
        return new DirectedGraph<T>(name, new HashSet<>(getVertices()), new HashMap<>(getEdges()));
    }

    public void reverseEdges() {
        Map<T, Set<T>> placeholder = edges;
        edges = reverseEdges;
        reverseEdges = placeholder;
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
