package edu.cornell.cs.cs4120.xic.ir;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.cornell.cs.cs4120.util.SExpPrinter;
import edu.cornell.cs.cs4120.xic.ir.visit.AggregateVisitor;
import edu.cornell.cs.cs4120.xic.ir.visit.IRVisitor;

/**
 * An intermediate representation for a compilation unit
 */
public class IRCompUnit extends IRNode_c {
    private String name;
    private Map<String, IRFuncDecl> functions;
    public Map<String, Integer> global_variables;
    public Map<String, Integer> global_variables_init;
    public Map<String, List<Long>> global_array_dim;

    public IRCompUnit(String name) {
        this.name = name;
        functions = new LinkedHashMap<>();
        global_variables = new LinkedHashMap<>();
        global_variables_init = new LinkedHashMap<>();
        global_array_dim = new LinkedHashMap<>();
    }

    public IRCompUnit(String name, Map<String, IRFuncDecl> functions) {
        this.name = name;
        this.functions = functions;
    }
    
    public IRCompUnit(String name, Map<String, IRFuncDecl> functions, IRCompUnit oldUnit) {
        this.name = name;
        this.functions = functions;
        global_variables = new LinkedHashMap<>(oldUnit.global_variables);
        global_variables_init = new LinkedHashMap<>(oldUnit.global_variables_init);
        global_array_dim = new LinkedHashMap<>(oldUnit.global_array_dim);
    }
    
    public Map<String, Integer> getGlobalVariablesMap() {
    		return global_variables;
    }

    public Map<String, Integer> getGlobalVariablesValueMap() {
    		return global_variables_init;
    }
    
    public void appendFunc(IRFuncDecl func) {
        functions.put(func.name(), func);
    }

    public void appendVarUninit(String name, int size) {
    		global_variables.put(name, size);
    }
    
    public void appendArray(String name, List<Long> dim) {
		global_variables.put(name, 1);
    		global_array_dim.put(name, dim);
    }
    
    public void appendVarInit(String name, int size, int value) {
    		global_variables.put(name,size);
    		global_variables_init.put(name, value);
    }
    
    public String name() {
        return name;
    }

    public Map<String, IRFuncDecl> functions() {
        return functions;
    }

    public IRFuncDecl getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public String label() {
        return "COMPUNIT";
    }

    @Override
    public IRNode visitChildren(IRVisitor v) {
        boolean modified = false;

        Map<String, IRFuncDecl> results = new LinkedHashMap<>();
        for (IRFuncDecl func : functions.values()) {
            IRFuncDecl newFunc = (IRFuncDecl) v.visit(this, func);
            if (newFunc != func) modified = true;
            results.put(newFunc.name(), newFunc);
        }

        if (modified) return v.nodeFactory().IRCompUnit(name, results);

        return this;
    }

    @Override
    public <T> T aggregateChildren(AggregateVisitor<T> v) {
        T result = v.unit();
        for (IRFuncDecl func : functions.values())
            result = v.bind(result, v.visit(func));
        return result;
    }

    @Override
    public void printSExp(SExpPrinter p) {
        p.startList();
        p.printAtom("COMPUNIT");
        try {
            p.printAtom(name);
        }
        catch (NullPointerException npe) {
            p.printAtom("NULL!");
        }
        for (IRFuncDecl func : functions.values()) {
            try {
                func.printSExp(p);
            }
            catch (NullPointerException npe) {
                p.printAtom("NULL!");
            }
        }
        p.endList();
    }
}
