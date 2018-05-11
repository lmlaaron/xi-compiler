package bsa52_ml2558_yz2369_yh326.ast.node.interfc;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;

public class InterfaceClass extends Interface {
    private Identifier id;
    public Identifier superClassId;

    public InterfaceClass(int line, int col, Identifier id) {
        super(line, col, id);
        this.id = id;
        this.superClassId = null;
    }
    
    public InterfaceClass(int line, int col, Identifier id, Identifier extend) {
        super(line, col, id);
        this.id = id;
        this.superClassId = extend;
    }
    
    @Override
    public void loadMethods(SymbolTable sTable) throws Exception {
        sTable.setCurClass(id.value);
        for (int i = 1; i < children.size(); i++)
            children.get(i).loadMethods(sTable);
        sTable.setCurClass(null);
    }
    
}
