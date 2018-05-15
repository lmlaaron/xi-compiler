package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.util.Utilities;

/**
 * implements functionalities that do not belong in Assembly.java, but will be
 * utilized by multiple AbstractAssemblyPipelines, etc.
 */
public class AssemblyUtils {
    // translate _ARG0, _RET0 to register or stack location
    static public AssemblyOperand ARGRET2Reg(AssemblyOperand name, int argc) {
        // System.out.print("ARGRET2Reg"+ name);
        // System.out.println(name.substring(0,4));
        // System.out.println("_ARG".length());
        if (name != null && name.value().length() >= "_ARG".length() && name.value().substring(0, 4).equals("_ARG")) {
            int v = Integer.valueOf(name.value().substring(4));
            switch (v) {
            case 0:
                return new AssemblyOperand("rdi");
            case 1:
                return new AssemblyOperand("rsi");
            case 2:
                return new AssemblyOperand("rdx");
            case 3:
                return new AssemblyOperand("rcx");
            case 4:
                return new AssemblyOperand("r8");
            case 5:
                return new AssemblyOperand("r9");
            default:
            }
            return AssemblyOperand.MemPlus("rbp", String.valueOf((v - 6 + 2) * 8));

        } else if (name != null && name.value().length() >= "_RET".length()
                && name.value().substring(0, 4).equals("_RET")) {
            int v = Integer.valueOf(name.value().substring(4));
            switch (v) {
            case 0:
                return new AssemblyOperand("rax");
            case 1:
                return new AssemblyOperand("rdx");
            default:
            }
            return AssemblyOperand.MemPlus("rsp", String.valueOf(((v - 2)) * 8));
            // rsp from caller pointer of view
        } else
            return name;
    }

    public static Set<String> use(AssemblyStatement stmt) {
        // equivalent to 'use' from lecture materials

        // it is correct to say a temp is used in every context in which it appears,
        // except where it is killed
        // HashSet<String> set = new HashSet<String>();
        // Arrays.stream(stmt.operands).forEach(op -> op.ResolveType());
        // if (stmt.operation.equals("mov")) {
        // AssemblyOperand dest = stmt.operands[0];
        // // don't want to say a temp is used if it's being assigned to
        // if (!dest.type.equals(AssemblyOperand.OperandType.TEMP)) {
        // set.addAll(dest.getTemps());
        // }
        // set.addAll(stmt.operands[1].getTemps());
        // }
        // // aside from mov, anywhere a temp is used everywhere it appears
        // else {
        // Arrays.stream(stmt.operands).forEach(op -> set.addAll(op.getTemps()));
        // }
        // return set;

        Set<String> ret = new HashSet<String>();
        Arrays.stream(stmt.operands).forEach(op -> op.ResolveType());
        switch (stmt.operation) {
        case "call":
            // call uses the registers which are used as arguments, and this depends on the
            // particular function
            int argc = getArgSize(stmt.operands[0].value());
            if (argc >= 1)
                ret.add("rdi");
            if (argc >= 2)
                ret.add("rsi");
            if (argc >= 3)
                ret.add("rdx");
            if (argc >= 4)
                ret.add("rcx");
            if (argc >= 5)
                ret.add("r8");
            if (argc >= 6)
                ret.add("r9");
            break;
        case "mov":
            if (stmt.operands[0].type == AssemblyOperand.OperandType.MEM)
                ret.addAll(stmt.operands[0].getEntities());
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "imul":
            if (stmt.operands.length == 2) {
                ret.addAll(stmt.operands[0].getEntities());
                ret.addAll(stmt.operands[1].getEntities());
            } else if (stmt.operands.length == 1) {
                ret.addAll(stmt.operands[0].getEntities());
                ret.add("rax");
            } else {
                throw new RuntimeException("Invalid number of operands for imul! " + stmt.operands.length);
            }
            break;
        case "cqo":
            ret.add("rax");
            break;
        case "idiv":
            ret.add("rax");
            ret.add("rdx");
            ret.addAll(stmt.operands[0].getEntities());
            break;
        case "lea":
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "add":
            ret.addAll(stmt.operands[0].getEntities());
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "sub":
            ret.addAll(stmt.operands[0].getEntities());
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "cmp":
            ret.addAll(stmt.operands[0].getEntities());
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "xor":
            ret.addAll(stmt.operands[0].getEntities());
            ret.addAll(stmt.operands[1].getEntities());
            break;
        case "push":
            ret.addAll(stmt.operands[0].getEntities());
            break;
        }

        // // print out values in the specific case where the temp is a_artmp$
        // if (Arrays.stream(stmt.operands).anyMatch( o ->
        // o.getEntities().contains("__FreshTemp_20"))) {
        // StringBuilder sb = new StringBuilder();
        // for (String s : ret)
        // sb.append(s + " ");
        // System.out.printf("USE: %-45s == {%s}%n", stmt, sb.toString());
        // }

        return ret;
    }

    public static Set<String> def(AssemblyStatement stmt) {
        // equivalent to 'def' from lecture materials

        // statements of the form MOV TEMP, *SOMETHING*
        // define TEMP

        // HashSet<String> set = new HashSet<String>();
        // if (stmt.operands.length != 2) return set;
        // else if (stmt.operation.equals("mov")) {
        // AssemblyOperand possiblyTemp = stmt.operands[0];
        //
        // possiblyTemp.ResolveType();
        // if (possiblyTemp.type.equals(AssemblyOperand.OperandType.TEMP)) {
        // set.add(possiblyTemp.value());
        // }
        // }
        Set<String> ret = new HashSet<>();
        Arrays.stream(stmt.operands).forEach(op -> op.ResolveType());
        switch (stmt.operation) {
        case "call":
            ret.addAll(Utilities.callerSaveRegisters());
            break;
        case "mov":
            if (stmt.operands[0].type != AssemblyOperand.OperandType.MEM)
                ret.addAll(stmt.operands[0].getEntities());
            break;
        case "imul":
            if (stmt.operands.length == 2) {
                ret.addAll(stmt.operands[0].getEntities());
            } else if (stmt.operands.length == 1) {
                ret.add("rdx");
                ret.add("rax");
            } else {
                throw new RuntimeException("Invalid number of operands for imul! " + stmt.operands.length);
            }
            break;
        case "cqo":
            ret.add("rdx");
            break;
        case "idiv":
            ret.add("rax");
            ret.add("rdx");
            break;
        case "lea":
            ret.addAll(stmt.operands[0].getEntities());
            break;
        case "add":
            ret.addAll(stmt.operands[0].getEntities());
            break;
        case "sub":
            ret.addAll(stmt.operands[0].getEntities());
            break;
        case "xor":
            ret.addAll(stmt.operands[0].getEntities());
            break;
        }

        // StringBuilder sb = new StringBuilder();
        // for (String s : ret)
        // sb.append(s + " ");
        // System.out.printf("DEF: %-45s == {%s}%n", stmt, sb.toString());
        // print out values in the specific case where the temp is a_artmp$
        // if (Arrays.stream(stmt.operands).anyMatch( o ->
        // o.getEntities().contains("a_irtmp$"))) {
        // StringBuilder sb = new StringBuilder();
        // for (String s : ret)
        // sb.append(s + " ");
        // System.out.printf("DEF: %-45s == {%s}%n", stmt, sb.toString());
        // }

        // // print out values in the specific case where the temp is a_artmp$
        // if (Arrays.stream(stmt.operands).anyMatch( o ->
        // o.getEntities().contains("__FreshTemp_20") )) {
        // StringBuilder sb = new StringBuilder();
        // for (String s : ret)
        // sb.append(s + " ");
        // System.out.printf("DEF: %-45s == {%s}%n", stmt, sb.toString());
        // }

        return ret;
    }

    public static void systemVEnforce(AssemblyFunction function) {
        if (!function.actuallyAFunction())
            return;

        ArrayList<Integer> lastCallArgC = argCountOfLastCall(function.statements);

        int thisFuncArgSize = AssemblyUtils.getArgSize(function.functionName);

        int stmt_i = 0;
        for (AssemblyStatement stmt : function.statements) {
            for (int i = 0; i < stmt.operands.length; i++) {
                AssemblyOperand op = stmt.operands[i];
                op.ResolveType();

                // Per the ReturnTile, replace __RETURN_X temps with the appropriate memory
                // location
                if (stmt.operands != null && stmt.operation.equals("mov")
                        && stmt.operands[0].type.equals(AssemblyOperand.OperandType.RET_UNRESOLVED)) {

                    // at this point, thisFuncArgSize NEEDS to be instantiated.
                    // if it isn't, we should throw an error
                    if (thisFuncArgSize == -1) {
                        throw new RuntimeException("Error: function arg size not instantiated before use!");
                    }

                    int index = stmt.operands[0].value().lastIndexOf("_");
                    int offset = Integer.valueOf(stmt.operands[0].value().substring(index + 1));
                    AssemblyOperand retOpt = null;
                    if (thisFuncArgSize <= 6) {
                        retOpt = AssemblyOperand.MemPlus("rbp", String.valueOf(offset * 8));
                    } else {
                        retOpt = AssemblyOperand.MemPlus("rbp", String.valueOf((offset + thisFuncArgSize - 6) * 8));
                    }
                    retOpt.type = AssemblyOperand.OperandType.REG_RESOLVED;
                    stmt.operands[0] = retOpt;
                }

                // also replace _ARGXX and _RETXX with registers, memory locations
                stmt.operands[i] = AssemblyUtils.ARGRET2Reg(stmt.operands[i], lastCallArgC.get(stmt_i));
                // List<String> temps = op.getTemps();
                //
                // List<String> newTemps = new LinkedList<String>();
                // for (String temp : temps) {
                // String convertedTemp = AssemblyUtils.ARGRET2Reg(temp,
                // lastCallArgC.get(stmt_i));
                // newTemps.add(convertedTemp);
                // }
                //
                // if (newTemps.size() > 0)
                // op.setTemps(newTemps);
            }

            stmt_i++;
        }
    }

    public static int getMaxStackOverHeadForFunctionCalls(List<AssemblyStatement> statements) {
        int ret = 0;

        for (AssemblyStatement stmt : statements) {
            // calculate the stacksize
            if (stmt.operation == "call") {
                int retSize = AssemblyUtils.getRetSize(stmt.operands[0].value());
                int argSize = AssemblyUtils.getArgSize(stmt.operands[0].value());

                ret = Math.max(retSize + argSize, ret);
            }
        }

        return ret;
    }

    protected static ArrayList<Integer> argCountOfLastCall(List<AssemblyStatement> statements) {
        ArrayList<Integer> ret = new ArrayList<>(statements.size());

        int argSize = 0;

        int lastCallArgc = 0;

        for (AssemblyStatement stmt : statements) {
            // calculate the stacksize
            if (stmt.operation == "call") {
                argSize = AssemblyUtils.getArgSize(stmt.operands[0].value());

                lastCallArgc = argSize;
            }

            ret.add(lastCallArgc);
        }

        return ret;
    }

    // per ABI specification, calculate the argument size
    static public int getArgSize(String targetName) {
        if (targetName.equals("_xi_out_of_bounds")) {
            return 0;
        } else if (targetName.equals("_xi_alloc")) {
            return 1;
        }
        try {
            int index = targetName.lastIndexOf("_");
            String sigStr = targetName.substring(index + 1);
            int num_ib = 0;
            for (char s : sigStr.toCharArray()) {
                if (s == 'i' || s == 'b') {
                    num_ib++;
                }
            }
            return num_ib - getRetSize(targetName);

        } catch (Exception e) {
            return 0;
        }
    }

    // per ABI specification, calculate the return size of a function
    static public int getRetSize(String targetName) {
        if (targetName.equals("_xi_out_of_bounds")) {
            return 0;
        } else if (targetName.equals("_xi_alloc")) {
            return 1;
        }
        try {
            int index = targetName.lastIndexOf("t");
            if (index != -1) { // assume less than 100 arguments
                if (targetName.toCharArray()[(index + 1)] == 'p') {
                    return 0;
                } else if (targetName.toCharArray()[(index + 2)] != 'a' && targetName.toCharArray()[(index + 2)] != 'b'
                        && targetName.toCharArray()[(index + 2)] != 'i') {
                    String v = targetName.substring(index + 1, index + 3);
                    return Integer.parseInt(v);
                } else {
                    return Integer.parseInt(targetName.substring(index + 1, index + 2));
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static HashSet<String> collectLabels(Assembly assm, boolean includeColon) {
        // collect all the label names, to prevent mistaking them for temps when
        // they appear as arguments (as with conditional jumps, etc)
        HashSet<String> labelNames = new HashSet<String>();

        // assumption: all labels we jump to will appear with a colon at least once
        // in the assembly
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.operation.endsWith(":")) {
                if (includeColon)
                    labelNames.add(stmt.operation);
                else
                    labelNames.add(stmt.operation.substring(0, stmt.operation.length() - 1));
            }
            // external functions won't be covered by previous branch
            else if (stmt.operation.equals("call")) {

                String dest = stmt.operands[0].value();

                // we need this extra check because temps are also valid arguments to call.
                // xi functions will always begin with '_', and we're making the assumption
                // that temp names will not
                if (dest.length() >= 1 && dest.substring(0,1).equals("_")) {
                    String label = stmt.operands[0].value();
                    if (includeColon)
                        label = label + ":";
                    labelNames.add(label);
                }

//                String label = stmt.operands[0].value();
//                if (includeColon)
//                    label = label + ":";
//                labelNames.add(label);
            }
        }

        return labelNames;
    }

    public static List<AssemblyFunction> partitionFunctions(Assembly assm) {
        List<AssemblyFunction> ret = new LinkedList<>();

        // the register allocation/spilling is based on the unit of functions,
        // we assume the entire abstract assembly is seperated by function call labels
        LinkedList<AssemblyStatement> FuncStatements = new LinkedList<>();
        // int sss =0;
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) { // per Xi ABI specification, the
                // we assume that function labels must start with "_I"
                ret.add(new AssemblyFunction(FuncStatements));
                FuncStatements = new LinkedList<>();
                FuncStatements.add(stmt);
            } else {
                FuncStatements.add(stmt);
            }
        }
        if (!FuncStatements.isEmpty())
            ret.add(new AssemblyFunction(FuncStatements));

        return ret;
    }
}
