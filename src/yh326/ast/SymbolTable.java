/**
 * 
 */
package yh326.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import yh326.ast.type.FunctionNodeType;
import yh326.ast.type.VariableNodeType;
import yh326.exception.FunctionAlreadyDefinedException;
import yh326.exception.VariableAlreadyDefinedException;
import yh326.util.Tuple;

/**
 * @author Syugen
 *
 */
public class SymbolTable {
    
    private Stack<String> logs;
    private Map<String, Stack<Tuple<VariableNodeType, Integer>>> varTable;
    private Map<String, FunctionNodeType> funcTable;
    private int level;
    
    /**
     * 
     */
    public SymbolTable() {
        logs = new Stack<String>();
        varTable = new HashMap<String, Stack<Tuple<VariableNodeType, Integer>>>();
        funcTable = new HashMap<String, FunctionNodeType>();
        level = 0;
    }
    
    /**
     * 
     */
    public void enterBlock() {
        logs.push(null);
        level++;
    }
    
    /**
     * 
     */
    public void exitBlock() {
        while (!logs.isEmpty()) {
            String last = logs.pop();
            if (last == null) {
                return;
            }
            else {
                Stack<Tuple<VariableNodeType, Integer>> value = varTable.get(last);
                value.pop();
                if (value.isEmpty()) {
                    varTable.remove(last);
                }
            }
        }
        level--;
    }
    
    /**
     * @param name
     * @param VariableType
     * @throws VariableAlreadyDefinedException
     */
    public void addVar(String name, VariableNodeType VariableType) throws VariableAlreadyDefinedException {
        logs.push(name);
        if (varTable.containsKey(name)) {
            if (varTable.get(name).peek().t2.intValue() == level) {
                throw new VariableAlreadyDefinedException();
            } else {
                varTable.get(name).push(new Tuple<VariableNodeType, Integer>(VariableType, level));
            }
        } else {
            Stack<Tuple<VariableNodeType, Integer>> value = new Stack<Tuple<VariableNodeType, Integer>>();
            value.push(new Tuple<VariableNodeType, Integer>(VariableType, level));
            varTable.put(name, value);
        }
    }
    
    /**
     * @param name
     * @param funcType
     * @throws FunctionAlreadyDefinedException
     */
    public void addFunc(String name, FunctionNodeType funcType)
            throws FunctionAlreadyDefinedException {
        if (funcTable.containsKey(name)) {
            throw new FunctionAlreadyDefinedException();
        } else {
            funcTable.put(name, funcType);
        }
    }
    
    /**
     * Given the name of a variable, return the type of that variable.
     * Return null if the variable is not defined in the symbol table.
     * @param varName The name of the variable
     * @return The type of the variable with the given name or null.
     */
    public VariableNodeType getVariableType(String varName) {
        if (varTable.containsKey(varName)) {
            return varTable.get(varName).peek().t1;
        } else {
            return null;
        }
    }
    
    /**
     * Given the name of a function, return the type of that function.
     * Return null if the function is not defined in the symbol table.
     * @param funcName The name of the function
     * @return The type of the function with the given name or null.
     */
    public FunctionNodeType getFunctionType(String funcName) {
        if (funcTable.containsKey(funcName)) {
            return funcTable.get(funcName);
        } else {
            return null;
        }
    }
    
    public void dumpTable() {
        System.out.println("Function table:");
        if (funcTable.keySet().size() == 0) {
            System.out.println("  Function table empty.");
        }
        for (String key : funcTable.keySet()) {
            System.out.println("  " + key + ": " + funcTable.get(key));
        }
        System.out.println("Variable table:");
        if (varTable.keySet().size() == 0) {
            System.out.println("  Variable table empty.");
        }
        for (String key : varTable.keySet()) {
            System.out.println("  " + key + ": " + varTable.get(key).toArray());
        }
    }
}
