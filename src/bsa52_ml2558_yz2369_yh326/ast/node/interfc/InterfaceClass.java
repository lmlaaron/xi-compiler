package bsa52_ml2558_yz2369_yh326.ast.node.interfc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.exception.AlreadyDefinedException;

public class InterfaceClass extends Interface {
    private Identifier id;
    public Identifier superClassId = null;

    public InterfaceClass(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
    }
    
    public InterfaceClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, id);
        this.id = id;
        this.superClassId = extend;
    }
    
    @Override
    public void loadClasses(SymbolTable sTable) throws Exception {
        XiClass newClass = new XiClass(line, col, id, superClassId);
        if (superClassId != null)
            newClass.superClass = sTable.getClass(superClassId.value);
        if (sTable.addClass(newClass) == false)
            throw new AlreadyDefinedException(line, col, id.value);
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        sTable.setCurClass(id.value);
        for (int i = 1; i < children.size(); i++)
            children.get(i).loadMethods(sTable);
        sTable.setCurClass(null);
    }
}
