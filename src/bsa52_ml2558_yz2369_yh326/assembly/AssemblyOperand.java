package bsa52_ml2558_yz2369_yh326.assembly;

import bsa52_ml2558_yz2369_yh326.ast.util.Utilities;

import java.util.*;

class OperandComparator implements Comparator<AssemblyOperand> {
    @Override
    public int compare(AssemblyOperand e1, AssemblyOperand e2) {
        if (e1.reorderIndex <= e2.reorderIndex) {
            return 1;
        } else {
            return -1;
        }
    }
}

/**
 * represents a single operand of an assembly statement. This class is helpful
 * because some tile's AssemblyStatement instances will be missing some operands
 * that they expect to get from their children.
 */
public class AssemblyOperand {
    protected String operand;
    public int reorderIndex; // for some tiles, the order of the operands in the generated assmbly are
                             // reordered, different from operands in IR

    /**
     * In a perfect world, we would have handled this using polymorphism. oh well.
     *
     * if true, this assemblyoperand instance is of one of the following forms:
     * [a + b*c]
     * [a + b]
     * [a]
     *
     * memOperandParts will contain [a [,b [,c]]].
     */
    protected boolean isMemOperand;
    protected ArrayList<String> memOperandParts;

    protected boolean memWrapped;

    public enum OperandType {
        TEMP, LABEL, MEM, CONSTANT, UNRESOLVED, REG_RESOLVED, RET_UNRESOLVED
    }

    public OperandType type;

    /**
     * MemWrapped operands are placeholders which, after being filled, are converted to
     * memory operands by wrapping with []
     */
    public static AssemblyOperand MemWrapped() {
        AssemblyOperand ao = new AssemblyOperand();
        ao.memWrapped = true;
        return ao;
    }

    /**
     * TODO: does this make MemWrapped() obsolete?
     *
     * @See isMemOperand
     *
     * We currently operate under the assumption that Mem operands don't need placeholders.
     * Because this function will be used with big tiles, we can assume that will hold.
     */
    public static AssemblyOperand Mem(String... parts) {
        assert parts.length > 1 && parts.length <= 3;

        StringBuilder repr = new StringBuilder();
        repr.append('[');
        repr.append(parts[0]);
        if (parts.length > 1) {
            repr.append(" + ");
            repr.append(parts[1]);
            if (parts.length > 2) {
                repr.append('*');
                repr.append(parts[2]);
            }
        }
        repr.append(']');

        AssemblyOperand ao = new AssemblyOperand(repr.toString());

        ao.isMemOperand = true;

        ao.memOperandParts = new ArrayList<String>(parts.length);
        for (int i = 0; i < parts.length; i++)
            ao.memOperandParts.add(parts[i]);

        ao.type = OperandType.MEM;

        return ao;
    }

    public AssemblyOperand(String op) {
        this.operand = op;
        this.reorderIndex = -1;
        this.type = OperandType.UNRESOLVED;
        this.ResolveType();
    }

    public AssemblyOperand(String op, OperandType t) {
        this.operand = op;
        this.reorderIndex = -1;
        this.type = t;
    }

    /**
     * resolve the type of operand by pattern matching
     */
    public void ResolveType() {
        if (this.type != OperandType.UNRESOLVED) {
            return;
        }

        // TODO fix this pattern matching but propagate from IR instead

        if ((this.operand.charAt(0) == '[') && (this.operand.charAt(this.operand.length() - 1) == ']')) {
            // } else if (this.operand.contains("[")){
            this.type = OperandType.MEM;
            // System.out.println(this.operand);
        } else if (this.operand.contains("__FreshTemp_") || this.operand.contains("_temp_")
                || this.operand.contains("_array_")) {
            this.type = OperandType.TEMP;
        }
        // this.type=OperandType.TEMP;
        // TODO implement other types

        /*if (this.operand.contains("__FreshTemp_") ||
                this.operand.contains("_temp_") ||
                this.operand.contains("_array_") ||
                this.operand.contains("_index_") ||
                
                ) {
            this.type = OperandType.TEMP;
        } else if ( this.operand.contains("FreshLabel") ||
                ) { 
        } else if () {
            
        } else if () {
            
        } else {
            this.type = OperandType.UNRESOLVED;
        }*/

    }

    public AssemblyOperand() {
        this.reorderIndex = -1;
        this.type = OperandType.UNRESOLVED;
    }

    public AssemblyOperand(int index) {
        this.reorderIndex = index;
        this.type = OperandType.UNRESOLVED;

    }

    /**
     * @returns whether this operand expects to have its contents filled by an
     *          external source
     */
    public boolean isPlaceholder() {
        return operand == null;
    }

    /**
     * @param operand
     *            the representation that this operand will assume
     */
    public void fillPlaceholder(String operand) {
        assert isPlaceholder();
        if (memWrapped)
            operand = "[" + operand + "]";
        this.operand = operand;
    }

    public void fillPlaceholder(AssemblyOperand operand) {
        this.type = operand.type;
        this.reorderIndex = operand.reorderIndex;
        fillPlaceholder(operand.operand);
    }

    /**
     * @return all registers that are a component of this operand.
     * Relies on ResolveType() already having been called
     */
    public List<String> getTemps() {
        if (isMemOperand) {
            LinkedList<String> registers = new LinkedList<String>();

            // Assuming elements are either constants or registers
            for (String s : memOperandParts) {
                if (!Utilities.isNumber(s) && !Utilities.isRealRegister(s)) {
                    registers.add(s);
                }
            }

            return registers;
        }
        else if (type == OperandType.MEM) {
            String val = value().substring(1, value().length()-1); // "[register]"
            LinkedList ret =  new LinkedList<String>();
            if (!Utilities.isRealRegister(val))
                ret.add(val);
            return ret;
        }
        else if (type == OperandType.TEMP) {
            LinkedList ret = new LinkedList<String>();
            if (!Utilities.isRealRegister(value()))
                ret.add(value());
            return ret;
        }
        else {
            return new LinkedList<String>();
        }
    }

    /**
     * Resets the value of all temps returned by getTemps().
     * Used during register allocation.
     *
     * @param registers the new values of the temp/registers. Must bee of same length
     *                  as getRegisters()
     */
    public void setTemps(List<String> registers) {
        if (isMemOperand) {
            ListIterator<String> it = registers.listIterator();

            for (int i = 0; i < memOperandParts.size(); i++) {
                String part = memOperandParts.get(i);
                if (!Utilities.isNumber(part) && !Utilities.isRealRegister(part)) {
                    memOperandParts.set(i, it.next());
                }
            }

            if (it.hasNext()) {
                throw new RuntimeException("Error: more registers were provided than can be used!");
            }
        }
        else if (type == OperandType.MEM) {
            assert registers.size() == 1;
            this.operand = "[" + registers.get(0) + "]";
        }
        else if (type == OperandType.TEMP) {
            assert registers.size() == 1;
            this.operand = registers.get(0);
        }
    }

    /**
     * @return the representation of this operand
     */
    public String value() {
        return operand;
    }

    @Override
    public String toString() {
        if (isPlaceholder()) {
            return "_";
        } else {
            return value();
        }
    }
}
