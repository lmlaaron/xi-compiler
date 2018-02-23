package yh326.ast.node;

import java.util.ArrayList;
import java.util.List;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;
import yh326.ast.SymbolTable;
import yh326.ast.exception.TypeErrorException;
import yh326.ast.type.Type;

/**
 * A abstract node in the AST of a program.
 * @author Syugen
 *
 */
public class Node {
    protected String value;
    protected List<Node> children;
    protected NodeDecoration decoration;

    /**
     * Use this constructor to construct a leaf node.
     * It can be a variable, an integer, a keyword, an operator, etc.
     * @param value The string representation of the node.
     */
    public Node(String value) {
        this.decoration = new NodeDecoration();
        this.value = value;
        this.children = null;
    }

    /**
     * Use this constructor to construct a non-leaf node.
     * @param nodes Children of the node to be constructed.
     */
    public Node(Node... nodes) {
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }

    /**
     * An abstract method that checks if the node has a valid type.
     * @param sTable the complete symbol table for the scope of this node
     * @return the Type of the node
     * @throws TypeErrorException if this or some child node has a type error
     */
    public Type typeCheck(SymbolTable sTable) throws TypeErrorException {
        throw new RuntimeException("Type Check Not Implemented!");
    }

    /**
     * Add given nodes as children of this node.
     * @param nodes The nodes to be added.
     */
    public void addNodes(Node... nodes) {
        for (Node node : nodes) {
            children.add(node);
        }
    }

    /**
     * Add children of the given node as children of this node.
     * @param node The node whose children are to be added.
     */
    public void addChildren(Node node) {
        if (node == null) return;
        for (Node child : node.children) {
            children.add(child);
        }
    }

    /**
     * Remove the outer most redundant parentheses.
     * @return The node after removing the outer most redundant parentheses.
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
    /**
     * Add the given node to the most left bottom position of the tree.
     * Note: this.children.get(0) is the "root";
     *       this.children.get(1) is the first child.
     * @param node The node to be added.
     */
    public void addHead(Node node) {
        Node cur = this;
        while (cur.children.get(1) != null) {
            cur = cur.children.get(1);
        }
        cur.children.set(1, node);
    }

    /**
     * Print the AST to System.out.
     * @param node The root of the AST to be printed.
     */
    public static void write(Node node) {
        OptimalCodeWriter writer = new OptimalCodeWriter(System.out, 40);
        SExpPrinter printer = new CodeWriterSExpPrinter(writer);
        writeRec(node, printer);
        printer.close();
    }

    /**
     * Helper function of write(Node).
     * @param node The root of the AST to be printed.
     * @param printer The printer.
     */
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

    @Override
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
