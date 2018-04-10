/**
 * 
 */
package bsa52_ml2558_yz2369_yh326.ast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.util.Tuple;

/**
 * @author Syugen
 *
 */
public class SymbolTable {

    private Stack<String> logs;
    private Map<String, Tuple<VariableType, Integer>> varTable;
    private Map<String, Tuple<NodeType, NodeType>> funcTable;
    private Set<String> funcImplemented;
    private String curFunc;
    private int level;

    /**
     * 
     */
    public SymbolTable() {
        logs = new Stack<String>();
        varTable = new HashMap<String, Tuple<VariableType, Integer>>();
        funcTable = new HashMap<String, Tuple<NodeType, NodeType>>();
        funcImplemented = new HashSet<String>();
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

    public boolean setImplemented(String name) {
        if (funcImplemented.contains(name)) {
            return false;
        } else {
            funcImplemented.add(name);
            return true;
        }
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
                varTable.remove(last);
            }
        }
        level--;
    }

    /**
     * @param name
     * @param VariableType
     */
    public boolean addVar(String name, VariableType VariableType) {
        if (funcTable.containsKey(name) || varTable.containsKey(name)) {
            return false;
        } else {
            Tuple<VariableType, Integer> value = new Tuple<VariableType, Integer>(VariableType, level);
            varTable.put(name, value);
            logs.push(name);
            return true;
        }
    }

    /**
     * @param name
     * @param funcType
     */
    public boolean addFunc(String name, List<VariableType> args, List<VariableType> rets) {
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

        if (funcTable.containsKey(name)) {
            // Functions with same name can appear, but must have same signature
            Tuple<NodeType, NodeType> type = funcTable.get(name);
            if (type.t1.equals(arg) && type.t2.equals(ret)) {
                return true;
            } else {
                return false;
            }
        } else {
            funcTable.put(name, new Tuple<NodeType, NodeType>(arg, ret));
            return true;
        }
    }

    /**
     * Given the name of a variable, return the type of that variable. Return null
     * if the variable is not defined in the symbol table.
     * 
     * @param varName
     *            The name of the variable
     * @return The type of the variable with the given name or null.
     */
    public VariableType getVariableType(String varName) {
        if (varTable.containsKey(varName)) {
            return varTable.get(varName).t1;
        } else {
            return null;
        }
    }

    /**
     * Given the name of a function, return the type of that function. Return null
     * if the function is not defined in the symbol table.
     * 
     * @param funcName
     *            The name of the function
     * @return The type of the function with the given name or null.
     * @throws FunctionNotDefinedException
     */
    public Tuple<NodeType, NodeType> getFunctionType(String funcName) {
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
            System.out.println("  " + key + ": " + varTable.get(key));
        }
        System.out.println("Log:");
        System.out.println("  " + Arrays.toString(logs.toArray()));
    }

    public int getLevel() {
        return level;
    }
}
