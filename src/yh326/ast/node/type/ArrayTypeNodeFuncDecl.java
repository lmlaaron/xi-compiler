package yh326.ast.node.type;

import yh326.ast.node.Bracket;

public class ArrayTypeNodeFuncDecl extends TypeNode {

    public ArrayTypeNodeFuncDecl(int line, int col) {
        super(line, col, new Bracket(line, col));
    }

}
