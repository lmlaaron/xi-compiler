package yh326;

import java.util.*;
import edu.cornell.cs.cs4120.util.*;
import polyglot.util.*;

public class Node {
    private String value;
    private List<Node> children;
    private boolean needParen;

    public Node(String value) {
        this.value = value;
        this.children = null;
        this.needParen = false;
    }

    public Node(Node... nodes) {
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }

    public Node addNodes(Node... nodes) {
        for (Node node : nodes) {
            children.add(node);
        }
        return this;
    }

    public Node addChildren(Node node) {
        if (node == null) return this;
        for (Node child : node.children) {
            children.add(child);
        }
        return this;
    }

    public static void write(Node node) {
        OptimalCodeWriter writer = new OptimalCodeWriter(System.out, 40);
        SExpPrinter printer = new CodeWriterSExpPrinter(writer);
        writeRec(node, printer);
        printer.close();
    }

    private static void writeRec(Node node, SExpPrinter printer) {
        if (node == null) {
            printer.startList();
            printer.endList();
        } else if (node.children == null) {
            printer.printAtom(node.value);
        } else {
            printer.startList();
            for (Node child : node.children) {
                writeRec(child, printer);
            }
            printer.endList();
        }
    }

    public String toString() {
        if (children == null) {
            return value;
        } else {
            String rs = "(";
            for (Node child : children) {
                rs += child.toString() + " ";
            }
            return rs.substring(0, rs.length() - 1) + ")";
        }
    }
}
