package bsa52_ml2558_yz2369_yh326.assembly;

import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;
import edu.cornell.cs.cs4120.util.Copy;

import java.util.*;
import java.util.stream.Collectors;

public class AssemblyFunction extends Assembly {

    /**
     * In cases where a block of assembly is not actually a function,
     * functionName will not be given a value in the constructor
     */
    protected String functionName;

    protected long id;

    public AssemblyFunction(LinkedList<AssemblyStatement> statements) {
        super(statements);

        id = NumberGetter.uniqueNumber();

        for (AssemblyStatement stmt : statements) {
            if (stmt.operation.length() >= 2 && stmt.operation.substring(0,2).equals("_I")) {
                functionName = stmt.operation.substring(0, stmt.operation.length()-1);
            }
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setStackSize(int stackSize) {
        if (!actuallyAFunction()) return;

        for (AssemblyStatement stmt : statements) {
            // replace STACKSIZE with the real size
            if (stmt.operands != null && stmt.operation.equals("sub") && stmt.operands[1].value().equals("STACKSIZE")) {
                stmt.operands[1] = new AssemblyOperand(String.valueOf(stackSize));
                return;
            }
        }
    }

    public void callerSave(Map<String, String> registerAllocations) {
//        // wherever this function calls other functions, it must save all caller save registers it uses
//        if (!actuallyAFunction()) return;
//
//        LinkedList<String> callerSave = new LinkedList<>(
//            registerAllocations.values().stream().filter(
//                r -> Utilities.isCallerSave(r)
//            ).collect(Collectors.toSet())
//        );
//
//        callerSave.remove("rax");
//        callerSave.remove("rdx");
//        // ^^^ HOWEVER rax and rdx are can be used as return values. for now, we'll just say that
//        // we never push them before or pop them after. AssemblyUtils.def() is consistent
//        // with this behavior
//        // TODO: alter pushes based on number of arguments of the called function AND
//        //       change gen()/kill() to reflect this change
//
//        System.out.println("===== CALLER SAVE FOR " + functionName);
//        System.out.println("all registers used in function:");
//        for (String r : registerAllocations.values())
//            System.out.println("\t" + r);
//        System.out.println("caller save registers used in function:");
//        for (String r : callerSave)
//            System.out.println("\t" + r);
//        System.out.println();
//
//        if (callerSave.isEmpty()) return;
//
//        List<AssemblyStatement> pushes = new LinkedList<>();
//        for (String r : callerSave)
//            pushes.add(new AssemblyStatement("push", r));
//
//        Collections.reverse(callerSave);
//
//        List<AssemblyStatement> pops = new LinkedList<>();
//        for (String r : callerSave)
//            pops.add(new AssemblyStatement("pop", r));
//
//
//        ListIterator<AssemblyStatement> it = statements.listIterator();
//        while (it.hasNext()) {
//            AssemblyStatement stmt = it.next();
//            if (stmt.operation.equals("call")) {
//                System.out.println("Encountered call for " + stmt.operands[0].value());
//
//                it.previous();
//                for (AssemblyStatement commentPart : AssemblyStatement.comment("Caller Pushes for call to " + stmt.operands[0].value()))
//                    it.add(commentPart);
//                for (AssemblyStatement push : pushes)
//                    it.add(push);
//                it.next();
//                for (AssemblyStatement commentPart : AssemblyStatement.comment("Caller Pops for call to " + stmt.operands[0].value()))
//                    it.add(commentPart);
//                for (AssemblyStatement pop : pops)
//                    it.add(pop);
//            }
//        }
    }

    public void calleeSave(Map<String, String> registerAllocations) {
        if (!actuallyAFunction()) return;



        // this function, as a callee, must save all callee-save variables it uses,
        // then restore them before each 'ret' statement
        List<String> calleeSave = new LinkedList<>(
            registerAllocations.values().stream().filter(
                r -> Utilities.isCalleeSave(r)
            ).collect(Collectors.toSet())
        );

        if (calleeSave.isEmpty()) return;

        // iterate to get past function label,
        //    push rbp
        //    mov rbp, rsp
        //    sub rsp, STACKSIZE
        ListIterator<AssemblyStatement> it = statements.listIterator();
        while (true) {
            AssemblyStatement stmt = it.next();
            if (Utilities.beginsWith(stmt.operation, "_I")) {
                while (!stmt.operation.equals("push")) {
                    stmt = it.next();
                }
                while (!stmt.operation.equals("mov")) {
                    stmt = it.next();
                }
                while (!stmt.operation.equals("sub")) {
                    stmt = it.next();
                }
                break;
            }
        }
        
        // special case: we don't want _I_init_ functions to calleeSave
        //               due to the layout of our stack
        if (Utilities.beginsWith(functionName, "_I_init_") || Utilities.beginsWith(functionName, "_I_ginit_")) {
            List<AssemblyStatement> saveStmts = new LinkedList<>();
            List<AssemblyStatement> loadStmts = new LinkedList<>();
        	    for (String reg:calleeSave) {
                    saveStmts.add(new AssemblyStatement("push", new AssemblyOperand(reg)));
                    loadStmts.add(new AssemblyStatement("pop", new AssemblyOperand(reg)));   	    	
        	    }
        	    Collections.reverse(loadStmts);
        	    // insert save statements:
                for (AssemblyStatement saveStmt : saveStmts)
                    it.add(saveStmt);
                
                      
            // now iterate through to every ret statement and add pop statements before:
            while (it.hasNext()) {
                AssemblyStatement statement = it.next();
                // leave always comes before ret.
                // because leave changes rbp, we must do restores before
                if (statement.operation.equals("leave")) {
                    it.previous();
                    for (AssemblyStatement loadStmt : loadStmts) {                   
                        	it.add(loadStmt);
                    }
                    it.next();
                }
            }
            return;
        }

        List<AssemblyStatement> saveStmts = new LinkedList<>();
        List<AssemblyStatement> loadStmts = new LinkedList<>();
        if (!functionName.equals("_Imain_paai")) {
            int i = 0;
            int base = 3;
            if (AssemblyUtils.getArgSize(functionName) > 6)
                base += AssemblyUtils.getArgSize(functionName) - 6;
            saveStmts.add(AssemblyStatement.comment("Callee Saves for " + functionName)[0]);
            loadStmts.add(AssemblyStatement.comment("Callee Restores for " + functionName)[0]);
            for (String reg : calleeSave) {
                AssemblyOperand stackLocation = AssemblyOperand.MemPlus("rbp", Integer.toString((i++ + base) * 8));
                saveStmts.add(new AssemblyStatement("mov", stackLocation, new AssemblyOperand(reg)));
                loadStmts.add(new AssemblyStatement("mov", new AssemblyOperand(reg), stackLocation));
            }
        }

        // insert save statements:
        for (AssemblyStatement saveStmt : saveStmts)
            it.add(saveStmt);


        // now iterate through to every ret statement and add pop statements before:
        while (it.hasNext()) {
            AssemblyStatement statement = it.next();
            // leave always comes before ret.
            // because leave changes rbp, we must do restores before
            if (statement.operation.equals("leave")) {
                it.previous();
                for (AssemblyStatement loadStmt : loadStmts)
                    it.add(loadStmt);
                it.next();
            }
        }

    }

    public boolean actuallyAFunction() {
        return functionName != null &&
                // pattern match against global variable names, which resemble functions
                !Utilities.beginsWith(functionName,"_I_g_") &&
                !Utilities.beginsWith(functionName, "_I_vt_") &&
                !Utilities.beginsWith(functionName, "_I_size_");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssemblyFunction that = (AssemblyFunction) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
