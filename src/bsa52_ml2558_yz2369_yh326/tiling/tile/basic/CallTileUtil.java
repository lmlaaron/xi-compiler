package bsa52_ml2558_yz2369_yh326.tiling.tile.basic;

import java.util.List;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyOperand;
import bsa52_ml2558_yz2369_yh326.assembly.AssemblyStatement;

public class CallTileUtil {
    public static void generateCallAssembly(List<AssemblyStatement> statements, int operandNum, String targetName) {
        // System V calling convention
        // move first 6 arguments in rdi, rsi, rdx, rcx, r8 and r9.
        if (operandNum > 0)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdi"), new AssemblyOperand()));
        if (operandNum > 1)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("rsi"), new AssemblyOperand()));
        if (operandNum > 2)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("rdx"), new AssemblyOperand()));
        if (operandNum > 3)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("rcx"), new AssemblyOperand()));
        if (operandNum > 4)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("r8"), new AssemblyOperand()));
        if (operandNum > 5)
            statements.add(new AssemblyStatement("mov", new AssemblyOperand("r9"), new AssemblyOperand()));

        // PUSH all other arguments onto stack
        for (int i = operandNum; i > 6; i--) {
            statements.add(new AssemblyStatement("push", new AssemblyOperand()));
        }
        statements.add(new AssemblyStatement("call", new AssemblyOperand(targetName)));

        // reduce the size of the stack
        if (operandNum > 6) {
            statements.add(new AssemblyStatement("add", new AssemblyOperand("rsp"),
                    new AssemblyOperand(String.valueOf(8 * (operandNum - 6)))));
        }

    }
}
