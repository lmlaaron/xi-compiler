package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp;
import edu.cornell.cs.cs4120.xic.ir.IRConst;
import edu.cornell.cs.cs4120.xic.ir.IRESeq;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRMem;
import edu.cornell.cs.cs4120.xic.ir.IRMove;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;
import edu.cornell.cs.cs4120.xic.ir.IRBinOp.OpType;

public class VarDecl extends Stmt {
	public VariableType VarType;
    public List<Identifier> ids;
    private TypeNode typeNode;
    private List<Expr> sizes;
    public boolean isInstanceVariable = false;
    public boolean isGlobalVariable = false;

    public VarDecl(int line, int col, Identifier id, TypeNode typeNode) {
        super(line, col, id, typeNode);
        this.ids = new ArrayList<>();
        this.ids.add(id);
        this.typeNode = typeNode;
    }
       
    public VarDecl(int line, int col, List<Identifier> ids, TypeNode typeNode) {
        super(line, col);
        this.children.addAll(ids);
        this.children.add(typeNode);
        this.ids = new ArrayList<>(ids);
        this.typeNode = typeNode;
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        return;
    }
    
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        typeCheckAndReturn(sTable);
        return new UnitType();
    }

    public NodeType typeCheckAndReturn(SymbolTable sTable) throws Exception {
        // Get NodeType from typeNode
        // For example, from Node ([] int) get NodeType int[]
        VariableType t = (VariableType) typeNode.typeCheck(sTable);
        VarType = t;
        for (Identifier id : ids)
            if (sTable.addVar(id.value, t, isInstanceVariable, isGlobalVariable) == false)
                throw new AlreadyDefinedException(line, col, id.value);
        sizes = t.getSizes();
        return t;
    }

    // TODO Mulong need to resolve the size if it is not constant
    public int getArraySize( Map<String, Long> globalIntSizeMap) {
    		int ret = 1;
    		if ( sizes == null ) {
    			return 1;
    		}
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i) == null)
                    return 1;

             IRExpr size = (IRExpr) sizes.get(i).translate();
             if (size instanceof IRConst ) {
                  ret = (int) (ret * ((IRConst) size).value());
             } else if (size instanceof IRName ) {
            	 	ret = (int) (ret * globalIntSizeMap.get( ((IRName) size).name()));
             } else if ( size instanceof IRTemp) {
         	 	ret = (int) (ret * globalIntSizeMap.get( ((IRTemp) size).name())); 	
             }else {
               return 1;
             }
       }
        return ret;
    }

    @Override
    public IRNode translate() {
        List<IRStmt> stmts = new ArrayList<IRStmt>();
        List<IRExpr> sizesExpr = new ArrayList<IRExpr>();
        
        if (sizes == null ) {
        		sizes = new ArrayList<>();
        		//sizes.add(new IntegerLiteral(line, col, "0"));
        }
        
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i) == null)
                break;

            IRExpr size = (IRExpr) sizes.get(i).translate();
            if (size instanceof IRConst || size instanceof IRTemp || size instanceof IRName) {
                sizesExpr.add(size);
            } else {
                IRTemp sizeTemp = new IRTemp("_temp_" + NumberGetter.uniqueNumberStr());
                stmts.add(new IRMove(sizeTemp, size));
                sizesExpr.add(sizeTemp);
            }

        }

        IRExpr t = generateIRNode(sizesExpr, 0);
        
        if (ids.size() == 1) {
            // Only 1 variable. "a: int"
            IRTemp var = new IRTemp(ids.get(0).getId());
            if (t == null) {
                return var;
            } else {
                stmts.add(new IRMove(var, t));
                return new IRESeq(new IRSeq(stmts), var);
            }
        } else {
            // More than one variable. "a, b: int".
            if (t == null) {
                return new IRSeq();
            } else {
                for (Identifier id : ids) {
                    IRTemp var = new IRTemp(id.getId());
                    stmts.add(new IRMove(var, t));
                }
                return new IRSeq(stmts);
            }
        }
        
    }

    private IRExpr generateIRNode(List<IRExpr> sizesExpr, int i) {
        if (i >= sizes.size() || sizes.get(i) == null) {
            return null;
        }
        
        IRESeq newArrayESeq = Utilities.xiAlloc(sizesExpr.get(i));
        List<IRStmt> stmts = ((IRSeq) newArrayESeq.stmt()).stmts();
        IRExpr newArray = newArrayESeq.expr();

        // The last bracket with value
        if (i + 1 >= sizes.size() || sizes.get(i + 1) == null) {
            return new IRESeq(new IRSeq(stmts), newArray);
        } else {
            IRTemp curIndex = new IRTemp("_index_" + NumberGetter.uniqueNumberStr());
            stmts.add(new IRMove(curIndex, new IRConst(0)));
            // the condition of the loop
            IRBinOp cond = new IRBinOp(OpType.LT, curIndex, sizesExpr.get(i));
            // the content of the loop
            List<IRStmt> then = new ArrayList<IRStmt>();
            IRMem memTo = new IRMem(
                    new IRBinOp(OpType.ADD, newArray, new IRBinOp(OpType.MUL, curIndex, new IRConst(8))));
            IRExpr memFrom = generateIRNode(sizesExpr, i + 1);
            then.add(new IRMove(memTo, memFrom));
            then.add(new IRMove(curIndex, new IRBinOp(OpType.ADD, curIndex, new IRConst(1))));
            stmts.add(While.getIRWhile(cond, new IRSeq(then)));
            return new IRESeq(new IRSeq(stmts), newArray);
        }
    }
}
