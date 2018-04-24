package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lmlaaron
 *
 */

/**
 * 
 * @author lmlaaron utilities for mapping temp variables in abstract assembly to
 *         rel variables int concrete assembly
 */
public class StackTable {
    /**
     * Register lookup table mapping the Abstract register to actual stack position
     */
    public Map<String, Integer> stackTable;
    public Integer globalCounter;

    public void SetCounter(Integer i) {
        globalCounter = i;
    }

    public StackTable() {
        globalCounter = 0;
        stackTable = new HashMap<String, Integer>();
    }

    public boolean isInTable(String op) {
        return (stackTable.containsKey(op));
    }

    public boolean add(String op) {
        if (isInTable(op)) {
            return false;
        } else {
            stackTable.put(op, globalCounter);
            globalCounter++;
            return true;
        }
    }

    public Integer MemIndex(String op) {
        if (!isInTable(op)) {
            return -1;
        } else {
            return stackTable.get(op);
        }
    }

    public int size() {
        return stackTable.size();
    }
}