package yh326.ast.node;

import yh326.ast.type.Type;

/**
 * During lexical analysis, passes are made over the AST which
 * add more information to the tree. This information should be stored in
 * this class
 */
public class NodeDecoration {
    protected Type nodeType;

    public NodeDecoration() {

    }

    public boolean hasType() { return nodeType != null; }
    public void setType(Type t) { nodeType = t; }
    public Type getType() { return nodeType; }

}
