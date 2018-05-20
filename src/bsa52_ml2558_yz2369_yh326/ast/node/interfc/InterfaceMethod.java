package bsa52_ml2558_yz2369_yh326.ast.node.interfc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.funcdecl.FunctionTypeDeclList;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.retval.RetvalList;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;
import bsa52_ml2558_yz2369_yh326.util.Utilities;

public class InterfaceMethod extends Interface {
    private Identifier id;
    private FunctionTypeDeclList args;
    private RetvalList rets;

    /**
     * Constructor
     * 
     * @param line
     * @param col
     * @param id
     * @param args
     * @param rets
     */
    public InterfaceMethod(int line, int col, Identifier id, FunctionTypeDeclList args, RetvalList rets) {
        super(line, col, id, args, rets);
        this.id = id;
        this.args = args;
        this.rets = rets;
    }

    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        // Interface and Method class share the same loadMethod method.
        // So it is moved to util package.
        if (Utilities.loadMethod(sTable, id.value, args, rets, true) == false) {
            throw new AlreadyDefinedException(line, col, id.value);
        }
        XiClass curClass = sTable.getCurClass();
        if (curClass != null)
            curClass.funcs_ordered.add(id.value);
    }
}
