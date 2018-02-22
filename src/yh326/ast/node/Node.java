package yh326.ast.node;

import java.util.ArrayList;
import java.util.List;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;
import yh326.ast.SymbolTable;
import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.Type;

public class Node {
    protected String value;
    protected List<Node> children;
    protected NodeDecoration decoration;

    public Node(String value) {
        this.decoration = new NodeDecoration();
        this.value = value;
        this.children = null;
    }

    public Node(Node... nodes) {
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }

    /*
    Add given nodes as children of this node.
     */
    public Node addNodes(Node... nodes) {
        for (Node node : nodes) {
            children.add(node);
        }
        return this;
    }

    /*
    Add children of the given node as children of this node.
     */
    public Node addChildren(Node node) {
        if (node == null) return this;
        for (Node child : node.children) {
            children.add(child);
        }
        return this;
    }

    /**
     * @param sTable the complete symbol table for the scope of this node
     * @return the Type of the node
     * @throws TypeErrorException if this or some child node has a type error
     */
    public Type typeCheck(SymbolTable sTable) throws TypeErrorException {
        throw new RuntimeException("Type Check Not Implemented!");
    }

    /*
    Remove the most outer redundant paranthesis.
     */
    public Node sub() {
        if (children == null) {
            return this;
        } else if (children.size() == 1) {
            return children.get(0);
        } else {
            return this;
        }
    }

    /*
    Add the given node to the most left bottom position of the tree.
    Note: this.children.get(0) is the root
          this.children.get(1) is the first child
     */
    public Node addHead(Node node) {
        Node cur = this;
        while (cur.children.get(1) != null) {
            cur = cur.children.get(1);
        }
        cur.children.set(1, node);
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
