package yh326;

import java.util.*;

public class Node {
    public String value;
    public List<Node> children;

    public Node(String value) {
        this.value = value;
        this.children = null;
    }

    public Node(Node... nodes) {
        this.value = null;
        this.children = new ArrayList<Node>();
        for (Node node : nodes) {
            this.children.add(node);
        }
    }

    public void addNode(Node node) {
        children.add(node);
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
