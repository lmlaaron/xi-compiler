package yh326.ast.node.literal;

import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;

public class IntegerLiteral extends ExprAtom {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param value
     */
    public IntegerLiteral(int line, int col, String value) {
        super(line, col, value);
    }

    @Override
    public IRNode translate() {
        return new IRConst(Integer.parseInt(this.value));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        return new VariableType(Primitives.INT);
    }
}
