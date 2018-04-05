package yh326.assembly;

import java.util.Arrays;
import java.util.Optional;

public class AssemblyStatement {
    public static AssemblyStatement[] comment(String comment) {
        String newline = System.getProperty("line.separator");

        String[] lines = comment.split(newline);

        AssemblyStatement[] ret = new AssemblyStatement[lines.length];
        for (int i = 0; i < lines.length; i++) {
            ret[i] = new AssemblyStatement(";" + lines[i]);
        }

        return ret;
    }


    String operation;
    AssemblyOperand[] operands;

    public AssemblyStatement(String operation) {
        this.operation = operation;
        operands = new AssemblyOperand[0];
    }
    public AssemblyStatement(String operation, String... operands) {
        this.operation = operation;
        this.operands = new AssemblyOperand[operands.length];
        for (int i = 0; i < operands.length; i++)
            this.operands[i] = new AssemblyOperand(operands[i]);
    }
    public AssemblyStatement(String operation, AssemblyOperand... operands) {
        this.operation = operation;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation);
        for (AssemblyOperand operand : operands) {
            sb.append(" ");
            sb.append(operand);
            sb.append(",");
        }
        if (operands.length > 0)
            sb.setLength(sb.length()-1); // remove final comma
        return sb.toString();
    }
}
