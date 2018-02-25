package yh326.ast.node;

import java.util.ArrayList;
import java.util.List;
import yh326.ast.SymbolTable;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitNodeType;
import yh326.exception.TypeErrorException;

/**
 * A abstract node in the AST of a program.
 * @author Syugen
 *
 */
public class Node {
    int line;
    int col;

    public String value;
    public List<Node> children;
    public NodeDecoration decoration;

    /**
     * Use this constructor to construct a leaf node.
     * It can be a variable, an integer, a keyword, an operator, etc.
     * @param value The string representation of the node.
     */
    public Node(int line, int col, String value) {
        this.line = line;
        this.col = col;
        this.decoration = new NodeDecoration();
        this.value = value;
        this.children = null;
    }

    /**
     * Use this constructor to construct a non-leaf node.
     * @param nodes Children of the node to be constructed.
     */
    public Node(int line, int col, Node... nodes) {
        this.line = line;
        this.col = col;
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }
    
    /**
     * Special case for Node(Node... nodes).
     */
    public Node(int line, int col) {
        this.line = line;
        this.col = col;
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
    }

    /**
     * Load methods in use statements and methods defined in the file into
     * the symbol table. This must be called before typeCheck is called.
     * "Use", "Interface", and "Method" class should override this method.
     * @param sTable the complete symbol table for the scope of this node
     * @throws Exception
     */
    public void loadMethods(SymbolTable sTable) throws Exception {
        //System.out.println("Entering " + this.getClass() + " " + this.line + " " + this.col);
        if (children != null) {
            for (Node child : children) {
                if (child != null) {
                    child.loadMethods(sTable);
                }
            }
        }
        //System.out.println("Exiting  " + this.getClass());
    }

    /**
     * Checks if the node has a valid type. Subclasses that involve variable 
     * or function declaration or using should rewrite this method. Others
     * (like "UseList", "Keyword" class, etc) don't need to rewrite.
     * NOTE: THIS METHOD MUST BE CALLED WHEN loadMethods HAS ALREADY BEEN CALLED.
     * @param sTable the complete symbol table for the scope of this node
     * @return the NodeType of the node
     * @throws Exception 
     */
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        System.out.println("Entering " + this.getClass() + " " + this.line + " " + this.col);
        NodeType type = new UnitNodeType();
        if (children != null) {
            for (Node child : children) {
                if (child != null) {
                    type = child.typeCheck(sTable);
                }
            }
        }
        System.out.println("Exiting  " + this.getClass());
        return type;
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
