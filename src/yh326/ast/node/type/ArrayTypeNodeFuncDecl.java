package yh326.ast.node.type;

import yh326.ast.node.Bracket;
import yh326.ast.node.Node;

public class ArrayTypeNodeFuncDecl extends TypeNode { // TODO: document - what is Emp?

    public ArrayTypeNodeFuncDecl(int line, int col) {
        super(line, col, new Bracket(line, col));
    }
    
    public ArrayTypeNodeFuncDecl(int line, int col, Node... node) {
        super(line, col, node);
    }

}
