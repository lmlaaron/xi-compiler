package yh326.ast.node.literal;

import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class StringLiteral extends ExprAtom {

    /**
     * Constructor
     * @param line
     * @param col
     * @param str
     */
    public StringLiteral(int line, int col, String str) {
        super(line, col, "\"" + str + "\"");
    }

    @Override
    public IRNode translate() {
    	// TODO: what is the translation for a string literal?
        throw new RuntimeException("Unknown translation for String Literal");
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = new VariableType(Primitives.INT, 1);
        return t;
    }
}
