package yh326.ast.node.expr;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import yh326.ast.SymbolTable;
import yh326.ast.node.Bracket;
import yh326.ast.node.Node;
import yh326.ast.node.stmt.If;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.exception.MatchTypeException;
import yh326.exception.OtherException;

public class Subscript extends Expr {

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param node
     */
    public Subscript(int line, int col, Node node) {
        // Node can only be identifier, method call, subscript.
        super(line, col, new Bracket(line, col), node);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        VariableType t = (VariableType) children.get(1).typeCheck(sTable);

        // Check if size (expr) is integer.
        VariableType expr = (VariableType) children.get(2).typeCheck(sTable);
        VariableType integer = new VariableType(Primitives.INT);

        if (expr.equals(integer)) {
            if (t.getLevel() == 0) {
                throw new OtherException(line, col, t + " is not subscriptable");
            } else {
                // Need to recursively restore the original level
                return new VariableType(t.getType(), t.getLevel() - 1);
            }
        } else {
            throw new MatchTypeException(line, col, integer, expr);
        }
    }

    @Override
    public IRNode translate() {
    		IRExpr var = (IRExpr) children.get(1).translate();
    		IRExpr index = (IRExpr) children.get(2).translate();
    		IRExpr len = new IRMem(new IRBinOp(OpType.SUB, var, new IRConst(8)));
    		IRExpr lt0 = new IRBinOp(OpType.LT, index, new IRConst(0));
    		IRExpr gtN = new IRBinOp(OpType.GEQ, index, len);
    		IRExpr cond = new IRBinOp(OpType.OR, lt0, gtN);
    		IRExpr then = new IRCall(new IRName("_xi_out_of_bounds"));
    		IRStmt boundCheck = If.getIRIf(cond, then);
        IRExpr res = new IRMem(new IRBinOp(OpType.ADD, var,
                new IRBinOp(OpType.MUL, new IRConst(8), index)));
        return new IRESeq(boundCheck, res);
    }

}
