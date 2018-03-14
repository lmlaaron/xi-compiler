package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.*;
import yh326.ast.SymbolTable;
import yh326.ast.node.Keyword;
import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.ast.type.VoidType;
import yh326.exception.MatchTypeException;
import yh326.exception.MismatchNumberException;

public class Return extends Stmt {

    public Return(int line, int col) {
        super(line, col, new Keyword(line, col, "return"));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType expected = sTable.getCurFuncReturnType();
        NodeType actual;
        
        // Nothing to be returned
        if (children.size() == 1) {
            actual = new UnitType();
        } else if (children.size() == 2) {
            actual = (VariableType) children.get(1).typeCheck(sTable);
        } else {
            List<VariableType> listType = new ArrayList<VariableType>();
            for (int i = 1; i < children.size(); i++) {
                listType.add((VariableType) children.get(i).typeCheck(sTable));
            }
            actual = new ListVariableType(listType);
        }
        
        if (actual.getClass() != expected.getClass()) {
            throw new MatchTypeException(line, col, expected, actual);
        } else {
            // Procedure
            if (actual instanceof UnitType) {
                return new VoidType();
            } else if (actual instanceof VariableType) {
                if (((VariableType) actual).equals((VariableType) expected)) {
                    return new VoidType();
                } else {
                    throw new MatchTypeException(line, col, expected, actual);
                }
            } else { // ListVariableType
                ListVariableType act = (ListVariableType) actual;
                ListVariableType exp = (ListVariableType) expected;
                if (act.getVariableTypes().size() != exp.getVariableTypes().size()) {
                    throw new MismatchNumberException(line, col,
                            exp.getVariableTypes().size(), act.getVariableTypes().size());
                } else if (act.equals(exp)) {
                    return new VoidType();
                } else {
                    throw new MatchTypeException(line, col, expected, actual);
                }
            }
        }
        
    }
    
    @Override
    public IRNode translate() {
    	// TODO: need to look into the structure of this return node
    	if (children.size() == 1) {
    		return new IRReturn(new ArrayList<IRExpr> ());
    	}
    	else if (children.size() >= 2) {

    	    // TODO: this translation operates under the assumption that IRReturn doesn't
            // do anything other than jump back to where the function was called, so return value
            // registers must be assigned to manually. Need to read more on Piazza...

    	    List<IRStmt> translation = new ArrayList<>();

    	    // translate retval children
    	    List<IRExpr> retvals = new ArrayList<>();
    		for (int i = 1; i < children.size(); i++) {
    			retvals.add((IRExpr) children.get(i).translate());
    		}

    		// each return value needs to be put in a TEMP to be loaded by calling function:
            int retSuffix = 0;
            for (IRExpr expr : retvals) {
                translation.add(
                        new IRMove(
                                new IRTemp("_RET" + retSuffix),
                                expr
                        )
                );
                retSuffix++;
            }

            // then return keyword
            translation.add(new IRReturn(retvals));

            return new IRSeq(translation);
    	}
    	else {
    		// Should have thrown an exception during type check
    		throw new RuntimeException("return node has zero children.");
    	}
    }
}
