package bsa52_ml2558_yz2369_yh326.assembly;

import bsa52_ml2558_yz2369_yh326.util.NumberGetter;

import java.util.Arrays;
import java.util.Optional;

public class AssemblyStatement {
    protected String id; // used to uniquely identify a statement, even in cases
                            // where instances are identical in content


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AssemblyStatement) {
            AssemblyStatement stmt = (AssemblyStatement)obj;
            return stmt.getID() == this.getID();
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getID() { return id; }

    public static AssemblyStatement[] comment(String comment) {
        String newline = System.getProperty("line.separator");

        String[] lines = comment.split(newline);

        AssemblyStatement[] ret = new AssemblyStatement[lines.length];
        for (int i = 0; i < lines.length; i++) {
            ret[i] = new AssemblyStatement("#" + lines[i]);
        }

        return ret;
    }

    public String operation;
    public boolean isFunctionLabel;
    public boolean isOtherLabel;
    public AssemblyOperand[] operands;

    public AssemblyStatement(String operation) {
        id = NumberGetter.uniqueNumberStr();

        isFunctionLabel = false;
        isOtherLabel = false;
        operands = new AssemblyOperand[0];

        this.operation = operation;
    }

    public AssemblyStatement(String operation, boolean funcLabel) {
        this(operation);
        isFunctionLabel = funcLabel;
        isOtherLabel = false;
    }

    public AssemblyStatement(String operation, String... operands) {
        this(operation);

        this.operands = new AssemblyOperand[operands.length];
        for (int i = 0; i < operands.length; i++)
            this.operands[i] = new AssemblyOperand(operands[i]);
    }

    public AssemblyStatement(String operation, AssemblyOperand... operands) {
        this(operation);
        this.operands = operands;
    }

    public boolean hasPlaceholder() {
        return Arrays.stream(operands).anyMatch(assmOp -> assmOp.isPlaceholder());
    }

    public AssemblyOperand getPlaceholder() {
        Optional<AssemblyOperand> opt = Arrays.stream(operands).filter(assmOp -> assmOp.isPlaceholder()).findFirst();
        return opt.orElse(null);
    }

    public void fillPlaceholder(String operand) {
        Optional<AssemblyOperand> opt = Arrays.stream(operands).filter(assmOp -> assmOp.isPlaceholder()).findFirst();
        assert opt.isPresent();
        opt.ifPresent(assmOp -> assmOp.fillPlaceholder(operand));
    }

    public void fillPlaceholder(AssemblyOperand operand) {
        Optional<AssemblyOperand> opt = Arrays.stream(operands).filter(assmOp -> assmOp.isPlaceholder()).findFirst();
        assert opt.isPresent();
        opt.ifPresent(assmOp -> assmOp.fillPlaceholder(operand));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.isFunctionLabel && !this.isOtherLabel) {
            sb.append("    ");
        }
        sb.append(operation);
        for (AssemblyOperand operand : operands) {
            sb.append(" ");
            sb.append(operand);
            sb.append(",");
        }
        if (operands.length > 0)
            sb.setLength(sb.length() - 1); // remove final comma
        return sb.toString();
    }
}
