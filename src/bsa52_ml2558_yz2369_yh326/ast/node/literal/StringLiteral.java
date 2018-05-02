package bsa52_ml2558_yz2369_yh326.ast.node.literal;

import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.Primitives;
import bsa52_ml2558_yz2369_yh326.ast.type.PrimitiveType;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class StringLiteral extends ExprAtom {

    private String str;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param str
     */
    public StringLiteral(int line, int col, String str) {
        super(line, col, "\"" + str + "\"");
    }

    @Override
    public IRNode translate() {
        IRESeq newArrayESeq = Utilities.xiAlloc(new IRConst(str.length()));
        List<IRStmt> stmts = ((IRSeq) newArrayESeq.stmt()).stmts();
        IRExpr newArray = newArrayESeq.expr();

        for (int i = 0; i < str.length(); i++) {
            IRMem mem = new IRMem(new IRBinOp(OpType.ADD, newArray, new IRConst(i * 8)));
            stmts.add(new IRMove(mem, new IRConst((int) str.charAt(i))));
        }
        return new IRESeq(new IRSeq(stmts), newArray);
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        value = value.replace("\\b", "\b").replace("\\t", "\t");
        value = value.replace("\\n", "\n").replace("\\f", "\f");
        value = value.replace("\\r", "\r").replace("\\\"", "\"");
        value = value.replace("\\\'", "\'").replace("\\\\", "\\");
        str = value.substring(1, value.length() - 1);
        PrimitiveType t = new PrimitiveType(Primitives.INT, 1);
        return t;
    }
}
