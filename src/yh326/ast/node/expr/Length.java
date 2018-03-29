package yh326.ast.node.expr;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.MatchTypeException;

public class Length extends Expr {
    private Expr expr;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param expr
     */
    public Length(int line, int col, Expr expr) {
        super(line, col, new Keyword(line, col, "length"), expr);
        this.expr = expr;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType type = (VariableType) expr.typeCheck(sTable);
        if (type.getLevel() >= 1) {
            return new VariableType(Primitives.INT);
        } else {
            throw new MatchTypeException(line, col, "int or bool array of any dimension", type);
        }
    }

    @Override
    public IRNode translate() {
        IRExpr irExpr = (IRExpr) expr.translate();
        return new IRMem(new IRBinOp(OpType.SUB, irExpr, new IRConst(8)));
    }
}
