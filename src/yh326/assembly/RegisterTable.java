package yh326.assembly;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author lmlaaron
 *
 */

/**
 * 
 * @author lmlaaron
 * utilities for mapping temp variables
 * in abstract assembly to rel variables 
 * int concrete assembly
 */
class RegisterTable {
	/**
	 * Register lookup table mapping the Abstract register
	 * to actual stack position
	 */
	public Map<String, Integer> registerTable;
	public Integer globalCounter;
	public void SetCounter(Integer i)  {
		globalCounter = i;
	}
	public RegisterTable() {
		globalCounter = 0;
		registerTable = new HashMap<String,Integer>();
	}
	public boolean isInTable(String op) {
		return (registerTable.containsKey(op));
	}
	public boolean add(String op) {
		if (isInTable(op)) {
			return false;
		} else {
			registerTable.put(op,globalCounter);
			globalCounter++;
			return true;
		}
	}
	public Integer MemIndex(String op) {
		if (!isInTable(op)) {
			return -1;
		} else {
			return registerTable.get(op);
		}
	}
}