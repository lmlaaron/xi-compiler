package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class AssemblyWrapper {
    public static Assembly GenerateAssembly(Tile tile, String outputFile) {
        Assembly abstractAssm = tile.generateAssembly();
        Assembly finalAssm = null;

        // For internal usage.
        try {
            if (Settings.genAbstract) { // write abstract assembly
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile + ".aasm")));
                writer.write(abstractAssm.toString());
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!Settings.disAsmGen) {
            finalAssm = processAbstractAssm(abstractAssm);
        }
        else {
            finalAssm = abstractAssm;
        }

        if (finalAssm.incomplete()) {
            System.out.println("Incomplete assembly code!:");
            System.out.println(finalAssm.toString());
            return finalAssm;
        }

        // write assembly to file
        if (Settings.disAsmGen)
            outputFile += ".ra.s";
        else
            outputFile += ".s";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
            writer.write(".intel_syntax noprefix " + "\n");
            // intel syntax annotation
            writer.write(finalAssm.toString());
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return finalAssm;
    }

    public static Assembly processAbstractAssm(Assembly assm) {
        HashSet<String> labelNames = collectLabels(assm);
        assm = registerAlloc(assm, labelNames);
        return assm;
    }

    public static HashSet<String> collectLabels(Assembly assm) {
        // collect all the label names, to prevent mistaking them for temps when
        // they appear as arguments (as with conditional jumps, etc)
        HashSet<String> labelNames = new HashSet<String>();

        // assumption: all labels we jump to will appear with a colon at least once
        //              in the assembly
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.operation.endsWith(":")) {
                labelNames.add(stmt.operation.substring(0, stmt.operation.length() - 1));
            }
        }

        return labelNames;
    }

    /**
     * top function for register allocation
     *
     * @return real assembly elimiating all temp
     */
    public static Assembly registerAlloc(Assembly assm, HashSet<String> labelNames) {
        LinkedList<AssemblyStatement> concreteStatements = new LinkedList<>();
        List<List<AssemblyStatement>> ListFuncStatements = new LinkedList<>();



        // TODO: REMOVE DEBUG MESSAGES
        // System.out.println("=== Label Names: ===");
        for (String label : labelNames) {
            // System.out.println(label);
        }
        // System.out.println();

        // the register allocation/spilling is based on the unit of functions,
        // we assume the entire abstract assembly is seperated by function call labels
        List<AssemblyStatement> FuncStatements = new LinkedList<>();
        // int sss =0;
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) { // per Xi ABI specification, the
                // function labels must start
                // with ``_I'', we assume that
                // vice versa
                // sss++;
                // System.out.println(stmt.toString()+"\n");
                ListFuncStatements.add(FuncStatements);
                FuncStatements = new LinkedList<>();
                FuncStatements.add(stmt);
                // System.out.println("Label: " + stmt);
            } else {
                FuncStatements.add(stmt);
            }
        }
        ListFuncStatements.add(FuncStatements);

        // // Debug printing:
        // int i = 1;
        // for (List<AssemblyStatement> func : ListFuncStatements) {
        //// System.out.println("Function " + i);
        // for (AssemblyStatement statement : func) {
        // System.out.println(statement);
        // }
        // System.out.println();
        // }

        // System.out.println("====================================");
        // System.out.println();

        // System.out.printf("sss "+String.valueOf(sss)+"\n");

        for (List<AssemblyStatement> oneFuncStatements : ListFuncStatements) {
            // System.out.println("Func Label : " + oneFuncStatements.get(0));

            int thisFuncArgSize = 0;
            // two-pass process
            // PASS 1: establish RegisterTable
            // PASS 2: replace register with respective stack location

            RegisterTable rTable = new RegisterTable();
            rTable.SetCounter(0); // set the counter which decides the position of the first spilled location on
            // the stack
            int retSize = 0;
            int argSize = 0;
            int maxSize = 0;

            int lastCallArgc = 0;

            ListIterator<AssemblyStatement> statementIt = oneFuncStatements.listIterator();
            // MARK 3
            while (statementIt.hasNext()) {
                AssemblyStatement stmt = statementIt.next();
                // find out the size of the allocated return space via the ABI
                // the return statement in this function body needs this value to find the stack
                // pointer to store the return value (like [rbp+...]
                if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) {
                    thisFuncArgSize = getArgSize(stmt.operation);
                }
                // calculate the stacksize
                if (stmt.operation == "call") {
                    retSize = getRetSize(stmt.operands[0].value());
                    argSize = getArgSize(stmt.operands[0].value());
                    if (retSize + argSize > maxSize) {
                        maxSize = retSize + argSize;
                    }
                    lastCallArgc = argSize;
                }

                // Per IR specification, replace _ARG0, _RET0 etc with respective register
                // MARK 4
                for (AssemblyOperand op : stmt.operands) {
                    op.ResolveType();

                    List<String> temps = op.getTemps();

                    List<String> newTemps = new LinkedList<String>();
                    List<Boolean> changed = new LinkedList<Boolean>();
                    for (String temp : temps) {
                        String convertedTemp = ARGRET2Reg(temp, lastCallArgc);
                        newTemps.add(convertedTemp);

                        if (temp.equals(convertedTemp)) {
                            changed.add(false);
                        } else {
                            changed.add(true);
                        }
                    }

                    // perform conversions for newTemps so that new memory locations
                    // are converted into temps
                    ListIterator<Boolean> changedIt = changed.listIterator();
                    ListIterator<String> newTempsIt = newTemps.listIterator();

                    statementIt.previous(); // move back by one so that cursor is before stmt
                    while (newTempsIt.hasNext()) {
                        String possiblyMemory = newTempsIt.next();
                        boolean didChange = changedIt.next();

                        /*
                         * if (didChange && possiblyMemory.charAt(0) == '[' ) { // the temp was replaced
                         * by a memory location. // just to be safe, we should convert the code so //
                         * that a temp can still be used String freshTemp = "__FreshTemp_" +
                         * NumberGetter.uniqueNumber();
                         *
                         * // possiblymemory is of form "[register+const]". we want the register and
                         * const... possiblyMemory = possiblyMemory.substring(1,
                         * possiblyMemory.length()-1); // cut off [] String[] parts =
                         * possiblyMemory.split("\\+");
                         *
                         * if (!(parts.length == 1 || parts.length == 2)) { throw new
                         * RuntimeException("Assumption failed!"); }
                         *
                         * statementIt.add(new AssemblyStatement("mov", new AssemblyOperand(freshTemp),
                         * AssemblyOperand.MemPlus(parts)));
                         *
                         * newTempsIt.set(freshTemp); // use freshTemp where possiblyMemory would have
                         * gone }
                         */
                    }
                    statementIt.next(); // move past stmt again

                    // TODO: remove try/catch
                    try {
                        if (newTemps.size() > 0)
                            op.setTemps(newTemps);
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println(
                                "error at operand '" + op.toString() + "' of statement '" + stmt.toString() + "'");
                        System.out.println("original temps: ");
                        for (String s : temps)
                            System.out.println(s + " ");
                        System.out.println("new temps:");
                        for (String s : newTemps)
                            System.out.println(s + " ");
                        System.exit(1);
                    }
                }
            }

            // establish the registerTable
            // MARK 5
            for (AssemblyStatement stmt : oneFuncStatements) {
                for (AssemblyOperand op : stmt.operands) {
                    op.ResolveType();

                    for (String temp : op.getTemps()) {
                        if (temp.contains("QWORD")) {
                            continue;
                        }
                        if (labelNames.contains(temp)) {
                            continue; // it's a label, not a temp. // TODO we should have a better way of expressing
                            // this
                        } else if (stmt.operation.equals("call")) {
                            continue; // external function names won't necessarily be a label in this file
                        }
                        if (temp.equals("STACKSIZE")) {
                            continue;
                        }

                        if (!rTable.isInTable(temp)) {
                            rTable.add(temp);
                            // System.out.println("Register Table Adding " + temp);
                        }
                    }
                }
            }

            // System.out.println("Function after first pass:");
            // for (AssemblyStatement statement : oneFuncStatements) {
            // System.out.println(statement);
            // }
            // System.out.println();

            // MARK 6
            for (AssemblyStatement stmt : oneFuncStatements) {

                // replace STACKSIZE with the real size
                if (stmt.operands != null && stmt.operation.equals("sub")
                        && stmt.operands[1].value().equals("STACKSIZE")) {
                    // System.out.println("rTable.size()"+ String.valueOf(rTable.size()));
                    // calculate the size (pad if not 16byte aligned)
                    int stacksize = rTable.size() + maxSize;
                    if (stacksize % 2 != 0) {
                        stacksize++;
                    }

                    stmt.operands[1] = new AssemblyOperand(String.valueOf(stacksize * 8));
                    concreteStatements.add(stmt);

                    continue;
                }
                // replace __RETURN_x (genereated using return tile) with the exact stack
                // location
                if (stmt.operands != null && stmt.operation.equals("mov")
                        && stmt.operands[0].type.equals(AssemblyOperand.OperandType.RET_UNRESOLVED)) {
                    int index = stmt.operands[0].operand.lastIndexOf("_");
                    int offset = Integer.valueOf(stmt.operands[0].operand.substring(index + 1));
                    AssemblyOperand retOpt = null;
                    if (thisFuncArgSize <= 6) {
                        retOpt = new AssemblyOperand("[rbp+" + String.valueOf((2 + offset - 2) * 8) + "]");
                    } else {
                        retOpt = new AssemblyOperand(
                                "[rbp+" + String.valueOf((2 + offset - 2 + thisFuncArgSize - 6) * 8) + "]");
                    }
                    retOpt.type = AssemblyOperand.OperandType.REG_RESOLVED;
                    stmt.operands[0] = retOpt;
                    // concreteStatements.add(stmt);
                    // continue;
                }

                // if the initial counter is 0, the ith register will be spilled to [rbp - 8 *
                // i]

                // TODO: support more than three registers
                LinkedList<String> availableRegisters = new LinkedList<>();
                availableRegisters.add("rbx");
                availableRegisters.add("rcx"); // TODO: Uncomment - it should work!
                availableRegisters.add("rdx");

                LinkedList<AssemblyStatement> loadStatements = new LinkedList<AssemblyStatement>();
                LinkedList<AssemblyStatement> saveStatements = new LinkedList<AssemblyStatement>();

                // MARK 7 : register allocation
                if (!stmt.operation.toLowerCase().equals("call")) {
                    // REGISTER ALLOCATION FOR THIS STATEMENT
                    for (int op_i = 0; op_i < stmt.operands.length; op_i++) {
                        AssemblyOperand op = stmt.operands[op_i];

                        op.ResolveType(); // just in case it wasn't resolved

                        List<String> tempReplacements = new LinkedList<String>();
                        List<String> temps = op.getTemps();
                        for (String temp : temps) {
                            if (labelNames.contains(temp)) {
                                tempReplacements.add(temp);
                                continue; // again, temp is actually a label and shouldn't be touched!
                            }

                            // TODO: debug printing:
                            if (temp.contains("rsp")) {
                                if (temp.contains("QWORD")) {
                                    continue;
                                }
                                // System.out.println("======================================");
                                // System.out.println("Operand: " + op);
                                // System.out.println("Operand Type: " + op.type);
                                // System.out.println();
                            }
                            if (temp.contains("rbp") && temp.contains("QWORD")) {
                                continue;
                            }

                            // allocate a physical register for temp
                            String allocatedRegister = availableRegisters.removeFirst();

                            // replace temp with this register
                            tempReplacements.add(allocatedRegister);

                            // location in memory corresponding to this temp
                            int mem_index = rTable.MemIndex(temp);
                            String memLocation = "QWORD PTR [rbp-" + String.valueOf(8 * mem_index) + "]";

                            // statements for storing this register before and saving it after
                            loadStatements.add(new AssemblyStatement("mov", allocatedRegister, memLocation));
                            saveStatements.add(new AssemblyStatement("mov", memLocation, allocatedRegister));
                        }
                        if (tempReplacements.size() > 0)
                            op.setTemps(tempReplacements);
                    }

                    // save all the generated statements
                    concreteStatements.addAll(loadStatements);
                    concreteStatements.add(stmt);
                    concreteStatements.addAll(saveStatements);
                } else {
                    concreteStatements.add(stmt);
                }

            }

        }
        return new Assembly(concreteStatements);
    }

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
}
