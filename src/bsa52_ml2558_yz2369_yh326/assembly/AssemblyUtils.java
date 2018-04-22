package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.HashSet;

/**
 * implements functionalities that do not belong in Assembly.java,
 * but will be utilized by multiple AbstractAssemblyPipelines, etc.
 */
public class AssemblyUtils {
    // translate _ARG0, _RET0 to register or stack location
    static public String ARGRET2Reg(String name, int argc) {
        // System.out.print("ARGRET2Reg"+ name);
        // System.out.println(name.substring(0,4));
        // System.out.println("_ARG".length());
        if (name != null && name.length() >= "_ARG".length() && name.substring(0, 4).equals("_ARG")) {
            int v = Integer.valueOf(name.substring(4));
            switch (v) {
                case 0:
                    return "rdi";
                case 1:
                    return "rsi";
                case 2:
                    return "rdx";
                case 3:
                    return "rcx";
                case 4:
                    return "r8";
                case 5:
                    return "r9";
                default:
            }
            String ret = "QWORD PTR [rbp+" + String.valueOf((v - 6 + 2) * 8) + "]"; // rbp from callee point of view
            // System.out.println("ARGRET2Reg"+ name+ ret);
            return ret;

        } else if (name != null && name.length() >= "_RET".length() && name.substring(0, 4).equals("_RET")) {
            int v = Integer.valueOf(name.substring(4));
            switch (v) {
                case 0:
                    return "rax";
                case 1:
                    return "rdx";
                default:
            }
            if (argc > 6) {
                String ret = "QWORD PTR [rsp+" + String.valueOf(((v - 2)) * 8) + "]";
                // System.out.println("ARGRET2Reg"+ name+ ret + "argc :"+ String.valueOf(argc));
                return ret;
            } else {
                String ret = "QWORD PTR [rsp+" + String.valueOf((v - 2) * 8) + "]";
                // System.out.println("ARGRET2Reg"+ name+ ret);
                return ret;
            }
            // rsp from caller pointer of view
        }
        return name;
    }

    // per ABI specification, calculate the argument size
    static public int getArgSize(String targetName) {
        if (targetName.equals("_xi_out_of_bounds")) {
            return 0;
        } else if (targetName.equals("_xi_alloc")) {
            return 1;
        }
        try {
            if (targetName != null) {
                int index = targetName.lastIndexOf("_");
                String sigStr = targetName.substring(index + 1);
                int num_a = 0;
                int num_ib = 0;
                for (char s : sigStr.toCharArray()) {
                    if (s == 'a') {
                        num_a++;
                    } else if (s == 'i' || s == 'b') {
                        num_ib++;
                    }
                }
                return num_ib - getRetSize(targetName);
            }
            return 0;
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
            if (targetName != null) {
                int index = targetName.lastIndexOf("t");
                if (index != -1) { // assume less than 100 arguments
                    if (targetName.toCharArray()[(index + 1)] == 'p') {
                        return 0;
                    } else if (targetName.toCharArray()[(index + 2)] != 'a'
                            && targetName.toCharArray()[(index + 2)] != 'b'
                            && targetName.toCharArray()[(index + 2)] != 'i') {
                        String v = targetName.substring(index + 1, index + 3);
                        return Integer.parseInt(v);
                    } else {
                        return Integer.parseInt(targetName.substring(index + 1, index + 2));
                    }
                }
                return 0;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static HashSet<String> collectLabels(Assembly assm, boolean includeColon) {
        // collect all the label names, to prevent mistaking them for temps when
        // they appear as arguments (as with conditional jumps, etc)
        HashSet<String> labelNames = new HashSet<String>();

        // assumption: all labels we jump to will appear with a colon at least once
        //              in the assembly
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.operation.endsWith(":")) {
                if (includeColon)
                    labelNames.add(stmt.operation);
                else
                    labelNames.add(stmt.operation.substring(0, stmt.operation.length() - 1));
            }
            // external functions won't be covered by previous branch
            else if (stmt.operation.equals("call")) {
                String label = stmt.operands[0].value();
                if (includeColon)
                    label = label + ":";
                labelNames.add(label);
            }
        }

        return labelNames;
    }
}
