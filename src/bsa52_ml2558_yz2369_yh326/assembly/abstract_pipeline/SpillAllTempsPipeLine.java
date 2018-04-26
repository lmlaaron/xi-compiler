package bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline;

import bsa52_ml2558_yz2369_yh326.assembly.*;
import bsa52_ml2558_yz2369_yh326.optimization.register_allocation.RegisterAllocation;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SpillAllTempsPipeLine implements AbstractAssemblyPipeline {
    @Override
    public Assembly process(Assembly abstractAssembly) {
        HashSet<String> labelNames = AssemblyUtils.collectLabels(abstractAssembly, false);
        List<List<AssemblyStatement>> functions = partitionFunctions(abstractAssembly);

        List<List<Integer>> lastCallArgCounts = getLastCallArgCounts(functions);
        List<Integer> maxStackSizes = getMaxStackSizes(functions);

        functions = systemVEnforce(functions, lastCallArgCounts);

        List<StackTable> stackTables = getRegisterTables(functions, labelNames);

        functions = specifyStackSizes(functions, stackTables, maxStackSizes);

        abstractAssembly = spillTempsOnStack(functions, labelNames, stackTables);
        return abstractAssembly;
    }

    /**
     * top function for register allocation
     *
     * @return real assembly elimiating all temp
     */
    public static Assembly spillTempsOnStack(List<List<AssemblyStatement>> functions, HashSet<String> labelNames, List<StackTable> rTables) {
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

    public List<List<AssemblyStatement>> specifyStackSizes(List<List<AssemblyStatement>> functions, List<StackTable> rTables, List<Integer> maxStackSizes) {
        int func_i = 0;
        for (List<AssemblyStatement> function : functions) {

            int thisFuncArgSize = -1; // -1 indicates not being instantiated

            for (AssemblyStatement stmt : function ) {

                if (thisFuncArgSize == -1) { // if the argsize hasn't been set yet, attempt to set it
                    if (stmt.isFunctionLabel && stmt.operation.substring(0, 2).equals("_I")) {
                        thisFuncArgSize = AssemblyUtils.getArgSize(stmt.operation);
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

                    stmt.operands[1] = new AssemblyOperand(String.valueOf((stacksize+2) * 8));
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

                    int index = stmt.operands[0].value().lastIndexOf("_");
                    int offset = Integer.valueOf(stmt.operands[0].value().substring(index + 1));
                    AssemblyOperand retOpt = null;
                    if (thisFuncArgSize <= 6) {
                        retOpt = AssemblyOperand.MemPlus("rbp", String.valueOf((offset * 8)));
                    } else {
                        retOpt = AssemblyOperand.MemPlus("rbp", String.valueOf((offset + thisFuncArgSize - 6) * 8));
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

    public static List<StackTable> getRegisterTables(List<List<AssemblyStatement>> functions, HashSet<String> labelNames) {
        List<StackTable> ret = new ArrayList<>(functions.size());

        // establish the stackTable
        for (List<AssemblyStatement> function : functions) {
            StackTable rTable = new StackTable();
            rTable.SetCounter(2);

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
                for (int i = 0; i < stmt.operands.length; i++) {
                    AssemblyOperand op = stmt.operands[i];
                    op.ResolveType();

                    stmt.operands[i] = AssemblyUtils.ARGRET2Reg(stmt.operands[i], lastCallArgCs.get(func_i).get(stmt_i));
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
                    retSize = AssemblyUtils.getRetSize(stmt.operands[0].value());
                    argSize = AssemblyUtils.getArgSize(stmt.operands[0].value());

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
                    argSize = AssemblyUtils.getArgSize(stmt.operands[0].value());

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




}
