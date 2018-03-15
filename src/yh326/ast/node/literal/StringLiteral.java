package yh326.ast.node.literal;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import yh326.ast.SymbolTable;
import yh326.ast.node.expr.ExprAtom;
import yh326.ast.type.NodeType;
import yh326.ast.type.Primitives;
import yh326.ast.type.VariableType;
import yh326.util.NumberGetter;

public class StringLiteral extends ExprAtom {
	
	private String str;

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
    	String name = "_array_" + NumberGetter.uniqueNumber();
    	List<IRStmt> stmts = new ArrayList<IRStmt>();
    	
    	// Allocate an array with size of children + 1 for length (each unit is 8 bytes)
    	IRCall call = new IRCall(new IRName("_xi_alloc"), new IRConst(str.length() * 8 + 8));
    	stmts.add(new IRMove(new IRTemp(name), new IRBinOp(OpType.ADD, call, new IRConst(8))));
    	
    	// Length is located at index of -1
    	IRBinOp indexNegOne = new IRBinOp(OpType.SUB, new IRTemp(name), new IRConst(8));
    	stmts.add(new IRMove(new IRMem(indexNegOne), new IRConst(str.length())));
		for (int i = 0; i < str.length(); i++) {
    		IRMem mem = new IRMem(new IRBinOp(OpType.ADD, new IRTemp(name), new IRConst(i * 8)));
    		stmts.add(new IRMove(mem, new IRConst((int) str.charAt(i))));
    	}
    	return new IRESeq(new IRSeq(stmts), new IRTemp(name));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
    	value = value.replace("\\b", "\b").replace("\\t", "\t");
        value = value.replace("\\n", "\n").replace("\\f", "\f");
        value = value.replace("\\r", "\r").replace("\\\"", "\"");
        value = value.replace("\\\'", "\'").replace("\\\\", "\\");
        str = value.substring(1, value.length() - 1);
        VariableType t = new VariableType(Primitives.INT, 1);
        return t;
    }
}
