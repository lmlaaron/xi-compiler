/**
 * 
 */
package yh326.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import yh326.ast.type.ListVariableType;
import yh326.ast.type.NodeType;
import yh326.ast.type.UnitType;
import yh326.ast.type.VariableType;
import yh326.exception.FunctionAlreadyDefinedException;
import yh326.exception.FunctionNotDefinedException;
import yh326.exception.VariableAlreadyDefinedException;
import yh326.exception.VariableNotDefinedException;
import yh326.util.Tuple;

/**
 * @author Syugen
 *
 */
public class SymbolTable {
    
    private Stack<String> logs;
    private Map<String, Stack<Tuple<VariableType, Integer>>> varTable;
    private Map<String, Tuple<NodeType, NodeType>> funcTable;
    private String curFunc;
    private int level;
    
    /**
     * 
     */
    public SymbolTable() {
        logs = new Stack<String>();
        varTable = new HashMap<String, Stack<Tuple<VariableType, Integer>>>();
        funcTable = new HashMap<String, Tuple<NodeType, NodeType>>();
        curFunc = null;
        level = 0;
    }
    
    public NodeType getCurFuncReturnType() {
        if (curFunc == null) {
            throw new RuntimeException("Unexpected error.");
        }
        return funcTable.get(curFunc).t2;
    }
    
    public void setCurFunction(String curFunc) {
        this.curFunc = curFunc;
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
                break;
            } else {
                Stack<Tuple<VariableType, Integer>> value = varTable.get(last);
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
    public void addVar(String name, VariableType VariableType) throws VariableAlreadyDefinedException {
        logs.push(name);
        if (varTable.containsKey(name)) {
            if (varTable.get(name).peek().t2.intValue() == level) {
                throw new VariableAlreadyDefinedException();
            } else {
                varTable.get(name).push(new Tuple<VariableType, Integer>(VariableType, level));
            }
        } else {
            Stack<Tuple<VariableType, Integer>> value = new Stack<Tuple<VariableType, Integer>>();
            value.push(new Tuple<VariableType, Integer>(VariableType, level));
            varTable.put(name, value);
        }
    }
    
    /**
     * @param name
     * @param funcType
     * @throws FunctionAlreadyDefinedException
     */
    public void addFunc(String name, List<VariableType> args, List<VariableType> rets)
            throws FunctionAlreadyDefinedException {
        if (funcTable.containsKey(name)) {
            throw new FunctionAlreadyDefinedException();
        } else {
            NodeType arg, ret;
            if (args.size() == 0) {
                arg = new UnitType();
            } else if (args.size() == 1) {
                arg = args.get(0);
            } else {
                arg = new ListVariableType(args);
            }
            if (rets.size() == 0) {
                ret = new UnitType();
            } else if (rets.size() == 1) {
                ret = rets.get(0);
            } else {
                ret = new ListVariableType(rets);
            }
            funcTable.put(name, new Tuple<NodeType, NodeType>(arg, ret));
        }
    }
    
    /**
     * Given the name of a variable, return the type of that variable.
     * Return null if the variable is not defined in the symbol table.
     * @param varName The name of the variable
     * @return The type of the variable with the given name or null.
     * @throws VariableNotDefinedException 
     */
    public VariableType getVariableType(String varName) throws VariableNotDefinedException {
        if (varTable.containsKey(varName)) {
            return varTable.get(varName).peek().t1;
        } else {
            throw new VariableNotDefinedException(varName);
        }
    }
    
    /**
     * Given the name of a function, return the type of that function.
     * Return null if the function is not defined in the symbol table.
     * @param funcName The name of the function
     * @return The type of the function with the given name or null.
     * @throws FunctionNotDefinedException 
     */
    public Tuple<NodeType, NodeType> getFunctionType(String funcName) throws FunctionNotDefinedException {
        if (funcTable.containsKey(funcName)) {
            return funcTable.get(funcName);
        } else {
            throw new FunctionNotDefinedException(funcName);
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
            System.out.println("  " + key + ": " + Arrays.toString(varTable.get(key).toArray()));
        }
        System.out.println("Log:");
        System.out.println("  " + Arrays.toString(logs.toArray()));
    }
    
    public int getLevel() {
        return level;
    }
}
