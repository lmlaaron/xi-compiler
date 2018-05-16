package bsa52_ml2558_yz2369_yh326.ast.node.method;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.StmtList;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.VarDecl;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRReturn;
import edu.cornell.cs.cs4120.xic.ir.IRSeq;
import edu.cornell.cs.cs4120.xic.ir.IRStmt;

public class Method extends Node {
    public Identifier id;
    private FunctionTypeDeclList args;
    private RetvalList rets;
    private StmtList block;
    private List<VariableType> argTypes;
    private List<VariableType> retTypes;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param id
     * @param args
     * @param rets
     * @param b
     */
    public Method(int line, int col, Identifier id, FunctionTypeDeclList args, RetvalList rets, StmtList b) {
        super(line, col, id, args, rets, b);
        this.id = id;
        this.args = args;
        this.rets = rets;
        this.block = b;
        this.argTypes = new ArrayList<>();
        this.retTypes = new ArrayList<>();
    }

    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        // Interface and Method class share the same loadMethod method.
        // So it is moved to util package.
        if (Utilities.loadMethod(sTable, id.value, args, rets) == false) {
            throw new AlreadyDefinedException(line, col, id.value);
        }
    }
   
    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        sTable.enterBlock();
        sTable.setCurFunction(id.value);

        // Check if this function has been implemented
        if (sTable.setFunctionImplemented(id.value) == false) {
            throw new OtherException(line, col, "This function has been implemented");
        }

        // Loading arguments into the symbol table
        if (args != null)
            for (Node varDecl : args.children)
                argTypes.add((VariableType) ((VarDecl) varDecl).typeCheckAndReturn(sTable));
        if (rets != null)
            for (Node varDecl : rets.children)
                retTypes.add((VariableType) ((TypeNode) varDecl).typeCheck(sTable));

        // Type check the statement list
        NodeType actual = new UnitType();
        NodeType expected = sTable.getFunctionType(id.value).t2;
        if (block != null) {
            actual = block.typeCheck(sTable);
        }
        if (actual instanceof UnitType && !(expected instanceof UnitType)) {
            throw new OtherException(line, col, "Missing return statement");
        }

        sTable.setCurFunction(null);
        sTable.exitBlock();
        return new UnitType();
    }
    
    // for object method, add this pointer to the method argument list
    public void addObjArgs(XiClass xc) {
    		List<VariableType>  retArgTypes = new ArrayList<>();
    		ObjectType thisType = new ObjectType(xc);
    		retArgTypes.add(thisType);
    		retArgTypes.addAll(this.argTypes);
    		this.argTypes = retArgTypes;
    		if (this.args == null ) {
    			
    			this.args = new FunctionTypeDeclList(line, col,
    					new VarDecl(line, col, new Identifier(line, col, "this"), new TypeNode(line, col, xc.classId.value)));
    		} else if (this.args.children.size() == 1) {
    			this.args.children.add( new VarDecl(line, col, new Identifier(line, col, "this"), new TypeNode(line, col, xc.classId.value)));
    		} else {
    			this.args.addHead(  new VarDecl(line, col, new Identifier(line, col, "this"), new TypeNode(line, col, xc.classId.value)));
    		}
    }

    @Override
    public IRNode translate() {
        String name = Utilities.toIRFunctionName(id.getId(), argTypes, retTypes);
        List<IRStmt> stmts = new ArrayList<IRStmt>();
        if (args != null) {
            stmts.addAll(((IRSeq) args.translate()).stmts());
        }
        stmts.addAll(((IRSeq) block.translate()).stmts());

        // If no return is given for a procedure, need to add one.
        if (stmts.size() == 0 || !(stmts.get(stmts.size() - 1) instanceof IRReturn)) {
            stmts.add(new IRReturn());
        }
        return new IRFuncDecl(name, new IRSeq(stmts));
    }
}
