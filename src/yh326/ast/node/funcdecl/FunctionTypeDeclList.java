package yh326.ast.node.funcdecl;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import yh326.ast.node.Node;
import yh326.ast.node.stmt.VarDecl;

/**
 * A node that represents a list of return variable declarations.
 * @author Syugen
 *
 */
public class FunctionTypeDeclList extends Node {
    /**
     * Constructor
     * @param line
     * @param col
     * @param varDecl
     */
    public FunctionTypeDeclList(int line, int col, VarDecl varDecl) {
        super(line, col, varDecl);
    }
    
    /**
     * Constructor
     * @param line
     * @param col
     */
    public FunctionTypeDeclList(int line, int col) {
        super(line, col);
    }
    
    @Override
    public IRNode translate() {
    	List<IRStmt> stmts = new ArrayList<IRStmt> ();
    	for (Node child : children) {
    		stmts.add((IRStmt) child.translate());
    	}
    	return new IRSeq(stmts);
    }
}
