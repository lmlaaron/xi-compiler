package bsa52_ml2558_yz2369_yh326.ast.node.funcdecl;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

/**
 * A node that represents a list of return variable declarations.
 * 
 * @author Syugen
 *
 */
public class FunctionTypeDeclList extends Node {
    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param varDecl
     */
    public FunctionTypeDeclList(int line, int col, VarDecl varDecl) {
        super(line, col, varDecl);
    }

    /**
     * Constructor
     * 
     * @param line
     * @param col
     */
    public FunctionTypeDeclList(int line, int col) {
        super(line, col);
    }

    @Override
    public IRNode translate() {
        List<IRStmt> stmts = new ArrayList<IRStmt>();
        for (int i = 0; i < children.size(); i++) {
        		System.out.println("children    "+ children.get(i).toString());
        		if ( children.get(i).translate() instanceof IRESeq) {
        			IRESeq id = (IRESeq) children.get(i).translate();
            		stmts.add(new IRMove(id, new IRTemp("_ARG" + i)));
        		} else {
        			IRTemp id = (IRTemp) children.get(i).translate();
            		stmts.add(new IRMove(id, new IRTemp("_ARG" + i)));
        		}
        }
        return new IRSeq(stmts);
    }
}
