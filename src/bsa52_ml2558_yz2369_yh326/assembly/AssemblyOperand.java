package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.Comparator;

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

    public enum OperandType {
        TEMP, LABEL, MEM, CONSTANT, UNRESOLVED, REG_RESOLVED, RET_UNRESOLVED
    }

    public OperandType type;

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
        this.operand = operand;
    }

    public void fillPlaceholder(AssemblyOperand operand) {
        this.operand = operand.operand;
        this.type = operand.type;
        this.reorderIndex = operand.reorderIndex;
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
