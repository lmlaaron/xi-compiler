/**
 * 
 */
package bsa52_ml2558_yz2369_yh326.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.Loop;
import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.ObjectType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.util.Tuple;

/**
 * @author Syugen
 *
 */
public class SymbolTable {
    
    private Stack<String> logs = new Stack<>();
    private Stack<Loop> loops = new Stack<>();
    private Map<String, XiClass> classTable = new HashMap<>();
    private Map<String, Tuple<VariableType, Integer>> varTable = new HashMap<>();
    private Map<String, Tuple<NodeType, NodeType>> funcTable = new HashMap<>();
    private Set<String> funcImplemented = new HashSet<>();
    private Set<String> interfaceImported = new HashSet<>();
    private Set<String> globalVars = new HashSet<>();
    private String curClass = null;
    private String curFunc = null;
    private int level = 0;
    
    public boolean isGlobalMethod(String name) {
        return funcTable.containsKey(name);
    }
    
    public boolean isGlobalVariable(String name) {
        return globalVars.contains(name);
    }
    
    public boolean containsClass(String name) {
        return classTable.containsKey(name);
    }

    public XiClass getClass(String name) {
        return classTable.get(name);
    }
    
    // =============== CURRENT CLASS ===============
    public XiClass getCurClass() {
        return classTable.get(curClass);
    }
    
    public void setCurClass(String curClass) {
        this.curClass = curClass;
    }

    // =============== CURRENT FUNCTION ===============
    public NodeType getCurFuncReturnType() {
        if (curFunc == null) {
            throw new RuntimeException("Unexpected error.");
        } else if (funcTable.containsKey(curFunc)) {
            return funcTable.get(curFunc).t2;
        } else {
            throw new RuntimeException("Unexpected error.");
        }
    }

    public void setCurFunction(String curFunc) {
        if (curClass != null)
            curFunc = "_" + curClass + "$" + curFunc;
        this.curFunc = curFunc;
    }
    
    // =============== FUNCTION IMPLEMENTED ===============
    public boolean setFunctionImplemented(String name) {
        if (curClass != null)
            name = "_" + curClass + "$" + name;
        if (funcImplemented.contains(name)) {
            return false;
        } else {
            funcImplemented.add(name);
            return true;
        }
    }
    
    // =============== INTERFACE IMPORTED ===============
    public boolean setInterfaceImported(String name) {
        if (interfaceImported.contains(name)) {
            return false;
        } else {
            interfaceImported.add(name);
            return true;
        }
    }

    // =============== ENTER / EXIT BLOCK ===============
    public void enterBlock() {
        logs.push(null);
        level++;
    }

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
    
    // =============== ENTER / EXIT LOOP ===============
    public void enterLoop(Loop loopNode) {
        loops.push(loopNode);
    }
    
    public void exitLoop() {
        loops.pop();
    }

    public Loop getLastLoop() {
        if (loops.isEmpty())
            return null;
        else
            return loops.peek();
    }
    
    // =============== ADD CLASS ===============
    public boolean addClass(XiClass xiClass) {
        if (classTable.containsKey(xiClass.classId.value)) {
            if (!xiClass.equals(classTable.get(xiClass.classId.value))) {
                return false;
            } else {
                xiClass.funcs_ordered = new ArrayList<>(classTable.get(xiClass.classId.value).funcs_ordered);
                xiClass.hasInterface = true;
                classTable.put(xiClass.classId.value, xiClass);
                return true;
            }
        } else {
            classTable.put(xiClass.classId.value, xiClass);
            return true;
        }
    }

    // =============== ADD VARIABLE ===============
    public boolean addVar(String name, VariableType variableType) {
        return addVar(name, variableType, false, false);
    }
    
    public boolean addVar(String name, VariableType variableType, 
            boolean isInstanceVariable, boolean isGlobalVariable) {
        if (isInstanceVariable)
        	    name = "_" + curClass + "$" + name;

        if (funcTable.containsKey(name) || varTable.containsKey(name)) {
            return false;
        } else {
        	    varTable.put(name, new Tuple<>(variableType, level));
        	    if (!isInstanceVariable) logs.push(name);
        	    if (isGlobalVariable) globalVars.add(name);
            return true;
        }
    }
    
    public void updateGlobalVar(String name, VariableType variableType) {
        varTable.get(name).t1 = variableType;
    }

    // =============== ADD FUNCTION ===============
    public boolean addFunc(String name, List<VariableType> args, List<VariableType> rets) {
        NodeType arg, ret;
        
        if (curClass != null) {
            args.add(0, new ObjectType(classTable.get(curClass)));
        }
        
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
        
        if (curClass != null)
            name = "_" + curClass + "$" + name;
        if (funcTable.containsKey(name)) {
            // Functions with same name can appear, but must have same signature
            Tuple<NodeType, NodeType> type = funcTable.get(name);
            if (type.t1.equals(arg) && type.t2.equals(ret)) {
                return true;
            } else {
                return false;
            }
        } else {
            funcTable.put(name, new Tuple<>(arg, ret));
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
        VariableType t = getVariableType(classTable.get(curClass), varName);
        if (t != null) return t;
        
        if (varTable.containsKey(varName)) {
            return varTable.get(varName).t1;
        } else {
            return null;
        }
    }
    
    public VariableType getVariableType(XiClass xiClass, String varName) {
        XiClass cur = xiClass;
        while (cur != null) {
            String classVar = "_" + cur.classId.value + "$" + varName;
            if (varTable.containsKey(classVar))
                return varTable.get(classVar).t1;
            cur = cur.superClass;
        }
        return null;
    }
    
    public VariableType getNonClassVariableType(String varName) {
        if (varTable.containsKey(varName))
            return varTable.get(varName).t1;
        else
            return null;
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
        Tuple<NodeType, NodeType> t = getFunctionType(classTable.get(curClass), funcName);
        if (t != null) return t;

        if (funcTable.containsKey(funcName)) {
            return funcTable.get(funcName);
        } else {
            return null;
        }
    }
    
    public Tuple<NodeType, NodeType> getFunctionType(XiClass xiClass, String funcName) {
        XiClass cur = xiClass;
        while (cur != null) {
            String classFunc = "_" + cur.classId.value + "$" + funcName;
            if (funcTable.containsKey(classFunc))
                return funcTable.get(classFunc);
            cur = cur.superClass;
        }
        return null;
    }
    
    public boolean isOverride(XiClass xiClass, String funcName) {
        XiClass cur = xiClass.superClass;
        while (cur != null) {
            String classFunc = "_" + cur.classId.value + "$" + funcName;
            if (funcTable.containsKey(classFunc))
                return true;
            cur = cur.superClass;
        }
        return false;
    }
    
    public void dumpTable() {
        System.out.println("====================");
        System.out.println("Current level: " + level);
        System.out.println("Current class: " + curClass);
        System.out.println("Current function: " + curFunc);
        System.out.print("Class list: ");
        if (classTable.keySet().size() == 0)
            System.out.println("Class table empty.");
        else
            System.out.println(classTable.keySet());
        
        System.out.println("Function table:");
        if (funcTable.keySet().size() == 0)
            System.out.println("  Function table empty.");
        for (String key : funcTable.keySet())
            System.out.println("  " + key + ": " + funcTable.get(key));

        System.out.println("Variable table:");
        if (varTable.keySet().size() == 0)
            System.out.println("  Variable table empty.");
        for (String key : varTable.keySet())
            System.out.println("  " + key + ": " + varTable.get(key));

        System.out.println("Log:\n  " + Arrays.toString(logs.toArray()));
        System.out.println("====================");
    }

    public int getLevel() {
        return level;
    }
}
