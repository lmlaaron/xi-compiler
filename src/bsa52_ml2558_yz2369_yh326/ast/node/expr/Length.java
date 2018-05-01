package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRConst;

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
