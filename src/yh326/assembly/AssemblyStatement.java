package yh326.assembly;

import java.util.Arrays;
import java.util.Optional;

public class AssemblyStatement {
    String operation;
    AssemblyOperand[] operands;

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

    public void fillPlaceholder(String operand) {
        Optional<AssemblyOperand> opt = Arrays.stream(operands).filter(assmOp -> assmOp.isPlaceholder()).findFirst();
        assert opt.isPresent();
        opt.ifPresent(assmOp -> assmOp.fillPlaceholder(operand));
    }
}
