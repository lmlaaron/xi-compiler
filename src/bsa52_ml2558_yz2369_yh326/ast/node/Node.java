package bsa52_ml2558_yz2369_yh326.ast.node;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.interfc.InterfaceClass;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.MethodClassList;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

/**
 * A abstract node in the AST of a program.
 * 
 * @author Syugen
 *
 */
public class Node {
    public int line;
    public int col;

    public String value;
    public List<Node> children;
    public NodeDecoration decoration;
    public boolean isInterface;
    public String fileName;

    /**
     * Use this constructor to construct a leaf node. It can be a variable, an
     * integer, a keyword, an operator, etc.
     * 
     * @param value
     *            The string representation of the node.
     */
    public Node(int line, int col, String value) {
        this.line = line;
        this.col = col;
        this.decoration = new NodeDecoration();
        this.value = value;
        this.children = null;
        this.isInterface = false;
    }

    /**
     * Use this constructor to construct a non-leaf node.
     * 
     * @param nodes
     *            Children of the node to be constructed.
     */
    public Node(int line, int col, Node... nodes) {
        this.line = line;
        this.col = col;
        this.decoration = new NodeDecoration();
        this.value = null;
        this.children = new ArrayList<Node>();
        this.isInterface = false;
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
        this.isInterface = false;
        this.children = new ArrayList<Node>();
    }

    /**
     * Load methods in use statements and methods defined in the file into the
     * symbol table. This must be called before typeCheck is called. "Use",
     * "Interface", and "Method" class should override this method.
     * 
     * @param sTable
     *            the complete symbol table for the scope of this node
     * @throws Exception
     */
    public void loadClasses(SymbolTable sTable, String libPath) throws Exception {
        for (Node child : children) {
            if (child != null) {
                child.loadClasses(sTable, libPath);
            }
        }
    }

    public void loadClasses(SymbolTable sTable) throws Exception {
        for (Node child : children) {
            if (child != null && (child instanceof XiClass || child instanceof InterfaceClass)) {
                child.loadClasses(sTable);
            }
        }
    }
    
    
    /**
     * Load methods in use statements and methods defined in the file into the
     * symbol table. This must be called before typeCheck is called. "Use",
     * "Interface", and "Method" class should override this method.
     * 
     * @param sTable
     *            the complete symbol table for the scope of this node
     * @throws Exception
     */
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        for (Node child : children) {
            if (child != null) {
                child.loadMethods(sTable, libPath);
            }
        }
    }

    public void loadMethods(SymbolTable sTable) throws Exception {
        for (Node child : children) {
            if (child != null) {
                child.loadMethods(sTable);
            }
        }
    }

    /**
     * Checks if the node has a valid type. Subclasses that involve variable or
     * function declaration or using should rewrite this method. Others (like
     * "UseList", "Keyword" class, etc) don't need to rewrite. NOTE: THIS METHOD
     * MUST BE CALLED WHEN loadMethods HAS ALREADY BEEN CALLED.
     * 
     * @param sTable
     *            the complete symbol table for the scope of this node
     * @return the NodeType of the node
     * @throws Exception
     */
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType type = new UnitType();
        if (children != null) {
            for (Node child : children) {
                if (child != null) {
                    type = child.typeCheck(sTable);
                }
            }
        }
        return type;
    }

    /**
     * Subclasses should override this method if it needs translation.
     * 
     * @return IRNode
     */
    public IRNode translate() {
        throw new RuntimeException("translate() not implemented for given subclass");
    }

    /**
     * This method should only be called by IRWrapper.
     * 
     * @return IRNode
     */
    public IRNode translateProgram() {
        for (Node child : children) {
            if (child != null && child instanceof MethodClassList) {
                child.fileName = fileName;
                return child.translate();
            }
        }
        return null;
    }

    /**
     * Add given nodes as children of this node.
     * 
     * @param nodes
     *            The nodes to be added.
     */
    public void addNodes(Node... nodes) {
        for (Node node : nodes) {
            children.add(node);
        }
    }

    /**
     * Add children of the given node as children of this node.
     * 
     * @param node
     *            The node whose children are to be added.
     */
    public void addChildren(Node node) {
        if (node == null)
            return;
        for (Node child : node.children) {
            children.add(child);
        }
    }

    /**
     * Remove the outer most redundant parentheses.
     * 
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
     * Add the given node to the most left bottom position of the tree. Note:
     * this.children.get(0) is the root this.children.get(1) is the first child
     */
    /**
     * Add the given node to the most left bottom position of the tree. Note:
     * this.children.get(0) is the "root"; this.children.get(1) is the first child.
     * 
     * @param node
     *            The node to be added.
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
