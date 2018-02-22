/**
 * 
 */
package yh326.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import yh326.ast.exception.FunctionAlreadyDefinedException;
import yh326.ast.exception.VariableAlreadyDefinedException;
import yh326.ast.type.FunctionType;
import yh326.ast.type.VarType;
import yh326.util.Tuple;

/**
 * @author Syugen
 *
 */
public class SymbolTable {
    
    private Stack<String> logs;
    private Map<String, Stack<Tuple<VarType, Integer>>> varTable;
    private Map<String, FunctionType> funcTable;
    private int level;
    
    /**
     * 
     */
    public SymbolTable() {
        logs = new Stack<String>();
        varTable = new HashMap<String, Stack<Tuple<VarType, Integer>>>();
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
                varTable.get(last).pop();
            }
        }
        level--;
    }
    
    /**
     * @param name
     * @param varType
     * @throws VariableAlreadyDefinedException
     */
    public void addVar(String name, VarType varType) throws VariableAlreadyDefinedException {
        logs.push(name);
        if (varTable.containsKey(name)) {
            if (varTable.get(name).peek().t2.intValue() == level) {
                throw new VariableAlreadyDefinedException();
            } else {
                varTable.get(name).push(new Tuple<VarType, Integer>(varType, level));
            }
        } else {
            Stack<Tuple<VarType, Integer>> value = new Stack<Tuple<VarType, Integer>>();
            value.push(new Tuple<VarType, Integer>(varType, level));
            varTable.put(name, value);
        }
    }
    
    /**
     * @param name
     * @param funcType
     * @throws FunctionAlreadyDefinedException
     */
    public void addFunc(String name, FunctionType funcType) throws FunctionAlreadyDefinedException {
        if (funcTable.containsKey(name)) {
            throw new FunctionAlreadyDefinedException();
        } else {
            funcTable.put(name, funcType);
        }
    }
}
