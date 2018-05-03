package bsa52_ml2558_yz2369_yh326.ast.node.expr;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.exception.MismatchNumberException;
import bsa52_ml2558_yz2369_yh326.exception.NotDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.Tuple;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRCall;
import edu.cornell.cs.cs4120.xic.ir.IRExpr;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class MethodCall extends Expr {
    private Identifier id;
    private List<VariableType> argTypes;
    private List<VariableType> retTypes;
    private XiClass classOfMethod;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param id
     */
    public MethodCall(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
        this.argTypes = new ArrayList<>();
        this.retTypes = new ArrayList<>();
        this.classOfMethod = null;
    }
    
    public void setClassOfMethod(XiClass xiClass) {
        classOfMethod = xiClass;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        // This method combined method call and procedure call.

        // First check if id is defined as a variable.
        if (sTable.getVariableType(id.value) != null) {
            throw new OtherException(line, col, id.value + " is not a function");
        }
        
        Tuple<NodeType, NodeType> funcType;
        if (classOfMethod != null)
            funcType = sTable.getFunctionType(classOfMethod, id.value);
        else
            funcType = sTable.getFunctionType(id.value);

        // See in what class is this method called
        XiClass curClass = classOfMethod;
        if (curClass == null) curClass = sTable.getCurClass();

        List<VariableType> actual = new ArrayList<VariableType>();
        if (curClass != null && !sTable.isGlobalMethod(id.value))
            actual.add(new ObjectType(curClass));
        for (int i = 1; i < children.size(); i++)
            actual.add((VariableType) children.get(i).typeCheck(sTable));
        
        if (funcType == null) {
            // Function not found
            throw new NotDefinedException(line, col, id.value);
        } else if (funcType.t1 instanceof UnitType) {
            // Function is a procedure
            if (actual.size() != 0) {
                throw new MismatchNumberException(line, col, 0, actual.size());
            }
        } else if (funcType.t1 instanceof VariableType) {
            // Function returns one value
            argTypes.add((VariableType) funcType.t1);
            if (actual.size() == 1) {
                if (!actual.get(0).isSubclassOf(funcType.t1)) {
                    throw new MatchTypeException(line, col, funcType.t1, actual.get(0));
                }
            } else {
                throw new MismatchNumberException(line, col, 1, actual.size());
            }
        } else {
            // Function returns multiple values
            argTypes = ((ListVariableType) funcType.t1).getVariableTypes();
            if (actual.size() != argTypes.size()) {
                throw new MismatchNumberException(line, col, argTypes.size(), actual.size());
            } else {
                for (int i = 0; i < actual.size(); i++) {
                    if (!actual.get(i).isSubclassOf(argTypes.get(i))) {
                        throw new MatchTypeException(line, col, argTypes.get(i), actual.get(i));
                    }
                }
            }
        }

        // Store return type
        if (funcType.t2 instanceof UnitType) {
            // Function is a procedure, do nothing
        } else if (funcType.t2 instanceof VariableType) {
            // Function returns one value
            retTypes.add((VariableType) funcType.t2);
        } else {
            retTypes = ((ListVariableType) funcType.t2).getVariableTypes();
        }
        return funcType.t2;
    }

    @Override
    public IRNode translate() {
        String name = Utilities.toIRFunctionName(id.getId(), argTypes, retTypes);
        List<IRExpr> exprs = new ArrayList<IRExpr>();
        for (int i = 1; i < children.size(); i++) {
            exprs.add((IRExpr) children.get(i).translate());
        }
        return new IRCall(new IRName(name), exprs);
    }
}
