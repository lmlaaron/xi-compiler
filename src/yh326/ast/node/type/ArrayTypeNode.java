package yh326.ast.node.type;

import yh326.ast.node.Bracket;
import yh326.ast.node.Node;

public class ArrayTypeNode extends TypeNode {

    public ArrayTypeNode(int line, int col) {
        super(line, col, new Bracket(line, col));
    }
    
    public ArrayTypeNode(int line, int col, Node... node) {
        super(line, col, node);
    }

}
