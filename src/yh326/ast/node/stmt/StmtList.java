package yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRExp;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VoidType;
import yh326.exception.MatchTypeException;

public class StmtList extends Stmt {

    public StmtList(int line, int col) {
        super(line, col);
    }
    
    public StmtList(int line, int col, Stmt stmt) {
        super(line, col, stmt);
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.enterBlock();
        NodeType type = new UnitType();
        
        // Check if all statement except for the last one has unit type.
        for (int i = 0; i < children.size() - 1; i++) {
            NodeType actual = children.get(i).typeCheck(sTable);
            if (!(actual instanceof UnitType)) {
                throw new MatchTypeException(line, col, type, actual);
            }
        }
        
        // Use the last statement's type as the return value.
        if (children.size() > 0) {
            type = children.get(children.size() - 1).typeCheck(sTable);
            if (!(type instanceof UnitType) && !(type instanceof VoidType)) {
                throw new MatchTypeException(line, col, "Unit or Void", type);
            }
        }
        sTable.exitBlock();
        return type;
    }
    
    @Override
    public IRNode translate() {
    	List<IRStmt> stmts = new ArrayList<> ();
    	for (Node child : children) {
    		IRNode node = child.translate();
    		if (node instanceof IRExpr) {
    			node = new IRExp((IRExpr) node);
    		}
    		stmts.add((IRStmt) node);
    	}
    	return new IRSeq(stmts);
    }

}
