package bsa52_ml2558_yz2369_yh326.ast.node.misc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.NotDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class Identifier extends Expr {
    private String id;
    private XiClass classOfInstance = null;
    private boolean isGlobalVariable = false;

    public Identifier(int line, int col, String id) {
        super(line, col, id);
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    public void setClassOfInstance(XiClass xiClass) {
        this.classOfInstance = xiClass;
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        XiClass curClass = sTable.getCurClass();
        
        if (id.equals("this")) {
            if (curClass != null) {
                return new ObjectType(curClass);
            } else {
                throw new OtherException(line, col, "\"this\" can only be used in class");
            }
        }
        
        VariableType type;
        if (classOfInstance != null) {
            type = sTable.getVariableType(classOfInstance, id);
        } else {
            type = sTable.getVariableType(id);
        }
        if (sTable.isGlobalVariable(id))
            this.isGlobalVariable = true;
        if (type != null) {
            return type;
        } else if (sTable.getFunctionType(id) != null) {
            throw new OtherException(line, col, id + " is not a variable");
        } else {
            throw new NotDefinedException(line, col, id);
        }
    }

    @Override
    public IRNode translate() {
        if (isGlobalVariable)
            return new IRName(id);
        else
            return new IRTemp(id);
    }
}
