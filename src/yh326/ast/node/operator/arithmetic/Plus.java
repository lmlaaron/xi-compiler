package yh326.ast.node.operator.arithmetic;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import yh326.ast.node.operator.arithmetic.ArithmeticOperator;
import yh326.ast.node.stmt.While;
import yh326.ast.type.NodeType;
import yh326.ast.type.VariableType;
import yh326.exception.OperandTypeException;
import yh326.util.NumberGetter;

public class Plus extends ArithmeticOperator {
	private boolean addingArray;
	private IRTemp newArray;
	private IRTemp a1;
	private IRTemp a2;
	private String labelNumber;
	
    public Plus(int line, int col) {
        super(line, col, "+");
    }

    @Override
    public NodeType returnTypeForOperandType(NodeType operandType) throws OperandTypeException {
        // first, check if operand type is an array. plus concatenates arrays
        if (operandType instanceof VariableType && ((VariableType)operandType).getLevel() > 0) {
        	addingArray = true;
            return operandType;
        } else {
        	addingArray = false;
        	return super.returnTypeForOperandType(operandType);
        }
    }

    @Override
    public IRNode translateWithOperands(IRExpr... operands) {
    	if (addingArray) {
    		labelNumber = NumberGetter.uniqueNumber();
    		newArray = new IRTemp("_array_" + labelNumber);
    		List<IRStmt> stmts = new ArrayList<IRStmt>();
        	
    		// Get old arrays and their sizes.
    		a1 = new IRTemp("_a1_" + labelNumber);
    		a2 = new IRTemp("_a2_" + labelNumber);
    		stmts.add(new IRMove(a1, operands[0]));
    		stmts.add(new IRMove(a2, operands[1]));
    		IRBinOp size1 = new IRBinOp(OpType.SUB, a1, new IRConst(8));
    		IRBinOp size2 = new IRBinOp(OpType.SUB, a2, new IRConst(8));
    		IRTemp size1Temp = new IRTemp("_a1_size_" + labelNumber);
    		IRTemp size2Temp = new IRTemp("_a2_size_" + labelNumber);
    		stmts.add(new IRMove(size1Temp, new IRMem(size1)));
    		stmts.add(new IRMove(size2Temp, new IRMem(size2)));
    		IRBinOp size = new IRBinOp(OpType.ADD, size1Temp, size2Temp);
    		IRBinOp sizePlusOne = new IRBinOp(OpType.ADD, 
    				new IRBinOp(OpType.MUL, size, new IRConst(8)), new IRConst(8));
    		
    		// Allocate new array
    		IRCall call = new IRCall(new IRName("_xi_alloc"), sizePlusOne);
        	stmts.add(new IRMove(newArray, new IRBinOp(OpType.ADD, call, new IRConst(8))));
        	// Length is located at index of -1
        	IRBinOp indexNegOne = new IRBinOp(OpType.SUB, newArray, new IRConst(8));
        	stmts.add(new IRMove(new IRMem(indexNegOne), size));
        	
        	// Loop through a1 and a2 and move values to new array
        	stmts.add(new IRMove(new IRTemp("_cur_index_" + labelNumber), new IRConst(0)));
        	copyArray(stmts, 1, size1Temp);
        	copyArray(stmts, 2, size2Temp);
        	return new IRESeq(new IRSeq(stmts), newArray);
    	} else {
    		return new IRBinOp(OpType.ADD, operands[0], operands[1]);
    	}
    }
    
    public void copyArray(List<IRStmt> stmts, int i, IRTemp oldArraySize) {
    	IRTemp curIndex = new IRTemp("_cur_index_" + labelNumber);
    	IRTemp curA1Index = new IRTemp((i == 1 ? "_a1_index_" : "_a2_index_") + labelNumber);
    	stmts.add(new IRMove(curA1Index, new IRConst(0)));
    	// the condition of the loop
    	IRBinOp cond = new IRBinOp(OpType.LT, curA1Index, oldArraySize);
    	// the content of the loop
    	List<IRStmt> then = new ArrayList<IRStmt>();
    	IRMem memTo = new IRMem(new IRBinOp(OpType.ADD, newArray, 
    			new IRBinOp(OpType.MUL, curIndex, new IRConst(8))));
    	IRMem memFrom = new IRMem(new IRBinOp(OpType.ADD, (i == 1 ? a1 : a2), 
    			new IRBinOp(OpType.MUL, curA1Index, new IRConst(8))));
    	then.add(new IRMove(memTo, memFrom));
    	then.add(new IRMove(curA1Index, new IRBinOp(OpType.ADD, curA1Index, new IRConst(1))));
    	then.add(new IRMove(curIndex, new IRBinOp(OpType.ADD, curIndex, new IRConst(1))));
    	stmts.add(While.getIRWhile(cond, new IRSeq(then)));
    }
}
