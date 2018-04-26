package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.*;

import bsa52_ml2558_yz2369_yh326.util.Utilities;

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
     * if true, this assemblyoperand instance is of one of the following forms: [a +
     * b*c] [a + b] [a]
     *
     * memOperandParts will contain [a [,b [,c]]].
     */
    protected boolean isMemOperand;
    protected ArrayList<String> memOperandParts;
    protected boolean memOperandPlus;

    protected boolean memWrapped;

    public enum OperandType {
        TEMP, LABEL, MEM, CONSTANT, UNRESOLVED, REG_RESOLVED, RET_UNRESOLVED
    }

    public OperandType type;

    /**
     * MemWrapped operands are placeholders which, after being filled, are converted
     * to memory operands by wrapping with []
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
     */
    public static AssemblyOperand MemPlus(String... parts) {
        assert parts.length > 1 && parts.length <= 3;

        AssemblyOperand ao = new AssemblyOperand();

        ao.memOperandParts = new ArrayList<String>(parts.length);
        for (int i = 0; i < parts.length; i++)
            ao.memOperandParts.add(parts[i]);

        ao.memOperandPlus = true;

        ao.operand = ao.MemRepr();

        ao.isMemOperand = true;

        ao.type = OperandType.MEM;

        return ao;
    }

    public static AssemblyOperand MemMinus(String... parts) {
        AssemblyOperand ao = AssemblyOperand.MemPlus(parts);
        ao.memOperandPlus = false;
        ao.operand = ao.MemRepr();

        return ao;
    }

    protected String MemRepr() {
        StringBuilder repr = new StringBuilder();

        repr.append('[');
        repr.append(memOperandParts.get(0));
        if (memOperandParts.size() > 1) {
            if (memOperandPlus)
                repr.append(" + ");
            else
                repr.append(" - ");

            repr.append(memOperandParts.get(1));
            if (memOperandParts.size() > 2) {
                repr.append('*');
                repr.append(memOperandParts.get(2));
            }
        }
        repr.append(']');

        return repr.toString();
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
        if (Utilities.isRealRegister(this.operand))
            this.type = OperandType.REG_RESOLVED;
        else if ((this.operand.charAt(0) == '[') && (this.operand.charAt(this.operand.length() - 1) == ']')) {
            // } else if (this.operand.contains("[")){
            this.type = OperandType.MEM;
            // System.out.println(this.operand);
        } else if (!Utilities.isRealRegister(this.operand) && !Utilities.isNumber(this.operand)) {
            this.type = OperandType.TEMP;
        }
        // this.type=OperandType.TEMP;
        // TODO implement other types

        /*
         * if (this.operand.contains("__FreshTemp_") || this.operand.contains("_temp_")
         * || this.operand.contains("_array_") || this.operand.contains("_index_") ||
         * 
         * ) { this.type = OperandType.TEMP; } else if (
         * this.operand.contains("FreshLabel") || ) { } else if () {
         * 
         * } else if () {
         * 
         * } else { this.type = OperandType.UNRESOLVED; }
         */

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

    protected boolean entityMatches(String s, boolean includeTemps, boolean includeRegisters) {
        if (!Utilities.isNumber(s)) {
            if (!includeRegisters && Utilities.isRealRegister(s)) {
                //System.out.printf("Entity %s IS a real register!%n", s);
                return false;
            }
            else if (!includeTemps && !Utilities.isRealRegister(s)) {
                //System.out.printf("Entity %s ISNT a real register!%n", s);
                return false;
            }
            else {
                //System.out.printf("Entity %s matches%n", s);
                return true;
            }
        }
        else {
            //System.out.printf("Entity %s is a number!%n", s);
            return false;
        }
    }

    protected void getEntityIf(List<String> entities, String s, boolean includeTemps, boolean includeRegisters) {
        if (entityMatches(s, includeTemps, includeRegisters))
            entities.add(s);
    }

    protected List<String> getEntities(boolean includeTemps, boolean includeRegisters) {
        ResolveType();

        LinkedList<String> entities = new LinkedList<String>();
        if (isMemOperand) {
            // Assuming elements are either constants or registers
            for (String s : memOperandParts) {
                getEntityIf(entities, s, includeTemps, includeRegisters);
            }
        } else if (type == OperandType.MEM) {
            String val = value().substring(1, value().length() - 1); // "[register]"
            getEntityIf(entities, val, includeTemps, includeRegisters);
        } else if (type == OperandType.TEMP) {
            getEntityIf(entities, value(), includeTemps, includeRegisters);
        }
        else if (type == OperandType.REG_RESOLVED) {
            getEntityIf(entities, value(), includeTemps, includeRegisters);
        }
        else {
            //System.out.printf("TYPE -> %s%n", type);
        }

        return entities;
    }
    /**
     * @return all registers that are a component of this operand. Relies on
     *         ResolveType() already having been called
     */
    public List<String> getTemps() {
        return getEntities(true, false);
    }

    public List<String> getRegisters() {
        return getEntities(false, true);
    }

    public List<String> getEntities() {
//        System.out.println("=== GET ENTITIES ===");
        List<String> ret = getEntities(true, true);
//        System.out.println();
        return ret;
    }

    /**
     * Resets the value of all temps returned by getTemps(). Used during register
     * allocation.
     *
     * @param temps
     *            the new values of the temp/registers. Must bee of same length as
     *            getRegisters()
     */
    public void setTemps(List<String> temps) {
        setEntities(temps, true, false);
    }

    public void setRegisters(List<String> registers) {
        setEntities(registers, false, true);
    }

    public void setEntities(List<String> entities) {
        setEntities(entities, true, true);
    }

    protected void setEntities(List<String> entities, boolean containsTemps, boolean containsRegisters) {
        if (entities.isEmpty()) return;
        if (isMemOperand) {

            ListIterator<String> it = entities.listIterator();

            for (int i = 0; i < memOperandParts.size(); i++) {
                String part = memOperandParts.get(i);
                if (entityMatches(part, containsTemps, containsRegisters))
                    memOperandParts.set(i, it.next());
            }
            operand = MemRepr();


            if (it.hasNext()) {
                throw new RuntimeException("Error: more registers were provided than can be used!");
            }
        } else if (type == OperandType.MEM) {
            assert entities.size() == 1;
            this.operand = "[" + entities.get(0) + "]";
        } else if (type == OperandType.TEMP) {
            assert entities.size() == 1;
            this.operand = entities.get(0);
        }
        else if (type == OperandType.REG_RESOLVED)
            this.operand = entities.get(0);
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
