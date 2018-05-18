package bsa52_ml2558_yz2369_yh326.ast.node.misc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Dot;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.exception.NotDefinedException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.xic.ir.IRName;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.IRTemp;

public class Identifier extends Expr {
    private String id;
    private VariableType idType;
    private boolean isGlobalVariable = false;
    public XiClass classOfInstance = null;
    
    public Identifier(int line, int col, String id) {
        super(line, col, id);
        this.id = id;
    }

    public String getId() {
        return id;
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
        
        if (sTable.isGlobalVariable(id))
            this.isGlobalVariable = true;
        
        VariableType type;
        if (classOfInstance != null) {
            type = sTable.getVariableType(classOfInstance, id);
        } else {
            type = sTable.getNonClassVariableType(id);
            if (type == null) {
                this.classOfInstance = curClass;
                type = sTable.getVariableType(classOfInstance, id);
            }
        }
        
        if (type != null) {
        		idType = type;
            return type;
        } else if (sTable.getFunctionType(id) != null) {
            throw new OtherException(line, col, id + " is not a variable");
        } else {
            throw new NotDefinedException(line, col, id);
        }
    }

    @Override
    public IRNode translate() {
        if (isGlobalVariable) {
            return new IRName( Utilities.toIRGlobalName(id, idType ));
        } else if (classOfInstance != null) {
            return Dot.translateVariable(classOfInstance, new IRTemp("this"), id);
        } else
            return new IRTemp(id);
    }
}
