package bsa52_ml2558_yz2369_yh326.ast.node.type;

import bsa52_ml2558_yz2369_yh326.ast.node.Bracket;

public class ArrayTypeNode extends TypeNode {

    public ArrayTypeNode(int line, int col) {
        super(line, col, new Bracket(line, col));
    }

}
