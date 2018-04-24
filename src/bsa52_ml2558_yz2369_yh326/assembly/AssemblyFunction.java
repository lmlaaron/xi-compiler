package bsa52_ml2558_yz2369_yh326.assembly;

import bsa52_ml2558_yz2369_yh326.util.NumberGetter;
import bsa52_ml2558_yz2369_yh326.util.Utilities;

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
        // wherever this function calls other functions, it must save all caller save registers it uses
        if (!actuallyAFunction()) return;

        List<String> callerSave = new LinkedList<>(
            registerAllocations.values().stream().filter(
                r -> Utilities.isCallerSave(r)
            ).collect(Collectors.toSet())
        );

        if (callerSave.isEmpty()) return;

        List<AssemblyStatement> pushes = new LinkedList<>();
        for (String r : callerSave)
            pushes.add(new AssemblyStatement("push", r));

        Collections.reverse(callerSave);

        List<AssemblyStatement> pops = new LinkedList<>();
        for (String r : callerSave)
            pops.add(new AssemblyStatement("pop", r));


        ListIterator<AssemblyStatement> it = pushes.listIterator();
        while (it.hasNext()) {
            AssemblyStatement stmt = it.next();
            if (stmt.operation.equals("call")) {
                it.previous();
                for (AssemblyStatement push : pushes)
                    it.add(push);
                it.next();
                for (AssemblyStatement pop : pops)
                    it.add(pop);
            }
        }
    }

    public void calleeSave(Map<String, String> registerAllocations) {

        // TODO: do we need a special case for Main?

        if (!actuallyAFunction()) return;

        // this function, as a callee, must save all callee-save variables it uses,
        // then restore them before each 'ret' statement
        List<String> calleeSave = new LinkedList<>(
            registerAllocations.values().stream().filter(
                r -> Utilities.isCalleeSave(r)
            ).collect(Collectors.toSet())
        );

        if (calleeSave.isEmpty()) return;

        // iterate to get past function label
        ListIterator<AssemblyStatement> it = statements.listIterator();
        while (true) {
            AssemblyStatement stmt = it.next();
            if (stmt.operation.length() >=2 && stmt.operation.substring(0, 2).equals("_I"))
            {
                break;
            }
        }

        // insert push statements:
        for (String toPush : calleeSave) {
            it.add(new AssemblyStatement("push", toPush));
        }

        // reverse because pushes and pops should be done in reverse order
        Collections.reverse(calleeSave);

        // now iterate through to every ret statement and add pop statements before:
        while (it.hasNext()) {
            AssemblyStatement statement = it.next();
            if (statement.operation.equals("ret")) {
                it.previous();
                for (String toPop : calleeSave) {
                    it.add(new AssemblyStatement("pop", toPop));
                }
                it.next();
            }
        }

    }

    public boolean actuallyAFunction() {
        return functionName != null;
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
