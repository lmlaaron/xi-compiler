package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

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
        HashSet<String> labelNames = collectLabels(assm, false);
        List<List<AssemblyStatement>> functions = partitionFunctions(assm);

        List<List<Integer>> lastCallArgCounts = getLastCallArgCounts(functions);
        List<Integer> maxStackSizes = getMaxStackSizes(functions);

        functions = systemVEnforce(functions, lastCallArgCounts);

        List<RegisterTable> registerTables = getRegisterTables(functions, labelNames);

        functions = specifyStackSizes(functions, registerTables, maxStackSizes);

        assm = spillTempsOnStack(functions, labelNames, registerTables);
        return assm;
    }

    public static List<List<AssemblyStatement>> specifyStackSizes(List<List<AssemblyStatement>> functions, List<RegisterTable> rTables, List<Integer> maxStackSizes) {
        int func_i = 0;
        for (List<AssemblyStatement> function : functions) {

            int thisFuncArgSize = -1; // -1 indicates not being instantiated

            for (AssemblyStatement stmt : function ) {

                if (thisFuncArgSize == -1) { // if the argsize hasn't been set yet, attempt to set it
                    if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) {
                        thisFuncArgSize = getArgSize(stmt.operation);
                    }
                }

                // replace STACKSIZE with the real size
                if (stmt.operands != null && stmt.operation.equals("sub")
                        && stmt.operands[1].value().equals("STACKSIZE")) {
                    // System.out.println("rTable.size()"+ String.valueOf(rTable.size()));
                    // calculate the size (pad if not 16byte aligned)
                    int stacksize = rTables.get(func_i).size() + maxStackSizes.get(func_i);
                    if (stacksize % 2 != 0) {
                        stacksize++;
                    }

                    stmt.operands[1] = new AssemblyOperand(String.valueOf(stacksize * 8));
                }
                // replace __RETURN_x (genereated using return tile) with the exact stack
                // location
                if (stmt.operands != null && stmt.operation.equals("mov")
                        && stmt.operands[0].type.equals(AssemblyOperand.OperandType.RET_UNRESOLVED)) {

                    // at this point, thisFuncArgSize NEEDS to be instantiated.
                    //  if it isn't, we should throw an error
                    if (thisFuncArgSize == -1){
                        throw new RuntimeException("Error: function arg size not instantiated before use!");
                    }

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
            }
            func_i++;
        }

        return functions;
    }

    public static List<RegisterTable> getRegisterTables(List<List<AssemblyStatement>> functions, HashSet<String> labelNames) {
        List<RegisterTable> ret = new ArrayList<>(functions.size());

        // establish the registerTable
        for (List<AssemblyStatement> function : functions) {
            RegisterTable rTable = new RegisterTable();
            rTable.SetCounter(0);

            for (AssemblyStatement stmt : function) {
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

            ret.add(rTable);
        }

        return ret;
    }

    public static List<List<AssemblyStatement>> systemVEnforce(List<List<AssemblyStatement>> functions, List<List<Integer>> lastCallArgCs) {
        int func_i = 0;
        for (List<AssemblyStatement> function : functions) {
            int stmt_i = 0;
            for (AssemblyStatement stmt : function) {
                for (AssemblyOperand op : stmt.operands) {
                    op.ResolveType();

                    List<String> temps = op.getTemps();

                    List<String> newTemps = new LinkedList<String>();
                    for (String temp : temps) {
                        String convertedTemp = ARGRET2Reg(temp, lastCallArgCs.get(func_i).get(stmt_i));
                        newTemps.add(convertedTemp);
                    }

                    if (newTemps.size() > 0)
                        op.setTemps(newTemps);
                }

                stmt_i++;
            }

            func_i++;
        }

        return functions;
    }

    // TODO: there's a lot of shared code between this and getLastCallArgCounts...
    //       is there any way to consolidate?
    public static List<Integer> getMaxStackSizes(List<List<AssemblyStatement>> functions) {
        List<Integer> ret = new ArrayList<>(functions.size());

        for (List<AssemblyStatement> function : functions) {

            int retSize = 0;
            int argSize = 0;
            int maxSize = 0;

            for (AssemblyStatement stmt : function) {
                // calculate the stacksize
                if (stmt.operation == "call") {
                    retSize = getRetSize(stmt.operands[0].value());
                    argSize = getArgSize(stmt.operands[0].value());

                    maxSize = Math.max(retSize+argSize, maxSize);
                }
            }

            ret.add(maxSize);
        }

        return ret;
    }

    public static List<List<Integer>> getLastCallArgCounts(List<List<AssemblyStatement>> functions) {
        List<List<Integer>> ret = new ArrayList<>(functions.size());

        for (List<AssemblyStatement> function : functions) {
            List<Integer> lastCallArgCount = new ArrayList<>(function.size());

            int argSize = 0;

            int lastCallArgc = 0;

            for (AssemblyStatement stmt : function) {
                // calculate the stacksize
                if (stmt.operation == "call") {
                    argSize = getArgSize(stmt.operands[0].value());

                    lastCallArgc = argSize;
                }

                lastCallArgCount.add(lastCallArgc);
            }

            ret.add(lastCallArgCount);
        }

        return ret;
    }

    public static List<List<AssemblyStatement>> partitionFunctions(Assembly assm) {
        List<List<AssemblyStatement>> ret = new LinkedList<List<AssemblyStatement>>();

        // the register allocation/spilling is based on the unit of functions,
        // we assume the entire abstract assembly is seperated by function call labels
        List<AssemblyStatement> FuncStatements = new LinkedList<>();
        // int sss =0;
        for (AssemblyStatement stmt : assm.statements) {
            if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) { // per Xi ABI specification, the
                // we assume that function labels must start with "_I"
                ret.add(FuncStatements);
                FuncStatements = new LinkedList<>();
                FuncStatements.add(stmt);
            } else {
                FuncStatements.add(stmt);
            }
        }
        ret.add(FuncStatements);

        return ret;
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
        }

        return labelNames;
    }



    /**
     * top function for register allocation
     *
     * @return real assembly elimiating all temp
     */
    public static Assembly spillTempsOnStack(List<List<AssemblyStatement>> functions, HashSet<String> labelNames, List<RegisterTable> rTables) {
        LinkedList<AssemblyStatement> concreteStatements = new LinkedList<>();

        int func_i = 0;
        int stmt_i = 0;
        for (List<AssemblyStatement> function : functions) {


            stmt_i = 0;
            for (AssemblyStatement stmt : function) {

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
                            int mem_index = rTables.get(func_i).MemIndex(temp);
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
                stmt_i++;
            }
            func_i++;
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
