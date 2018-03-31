package yh326.assembly;

/**
 * represents a single operand of an assembly statement. This class
 * is helpful because some tile's AssemblyStatement instances will be missing
 * some operands that they expect to get from their children.
 */
public class AssemblyOperand {
    protected String operand;

    public AssemblyOperand(String operand) {
        this.operand = operand;
    }

    public AssemblyOperand() { }

    /**
     * @returns whether this operand expects to have its contents filled
     * by an external source
     */
    public boolean isPlaceholder() {
        return operand == null;
    }

    /**
     * @param operand the representation that this operand will assume
     */
    public void fillPlaceholder(String operand) {
        assert isPlaceholder();
        this.operand = operand;
    }

    /**
     * @return the representation of this operand
     */
    public String value() {
        return operand;
    }
}
