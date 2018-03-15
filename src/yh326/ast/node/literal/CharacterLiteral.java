package yh326.ast.node.literal;

import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class CharacterLiteral extends ExprAtom {
    /**
     * Constructor
     * @param line
     * @param col
     * @param ch
     */
    public CharacterLiteral(int line, int col, String ch) {
        super(line, col, "\'" + ch + "\'");
    }

    @Override
    public IRNode translate() {
        // per the constructor, 'value' is the character itself enclosed by single quotes
        return new IRConst((int)value.charAt(1));
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        value = value.replace("\\b", "\b").replace("\\t", "\t");
        value = value.replace("\\n", "\n").replace("\\f", "\f");
        value = value.replace("\\r", "\r").replace("\\\"", "\"");
        value = value.replace("\\\'", "\'").replace("\\\\", "\\");
        return new VariableType(Primitives.INT);
    }

}
