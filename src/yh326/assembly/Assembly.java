package yh326.assembly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
Represents a block of assembly instructions, either complete or a fragment
introduced by a tile, which needs to be merged with child and parent assemblies.

When Assemblies are merged, it is not simple enough to prepend the lines from the
children before the lines of the parents. Often operations in a parent's statement
use arguments determined by one or more of the children. This is captured by the
filler attribute and merge() function.
 */
public class Assembly {
	/**
	 * Save the mapping of assembly operand to memory location by spilling
	 */
	RegisterTable rTable;
	
    /**
     * All other assembly besides filler
     */
    public LinkedList<AssemblyStatement> statements;
    /**
     * An operand from the current assembly, to be propagated upwards to
     * a placeholder in a parent assembly
     */
    public Optional<AssemblyOperand> filler;

    public Assembly(LinkedList<AssemblyStatement> statements) {
        this.statements = statements;
        this.filler = Optional.empty();
    }
    public Assembly(LinkedList<AssemblyStatement> statements, AssemblyOperand filler) {
        this.statements = statements;
        this.filler = Optional.of(filler);
    }
    public Assembly() {
        statements = new LinkedList<>();
        this.filler = Optional.empty();
    }
    public Assembly(AssemblyOperand filler) {
        statements = new LinkedList<>();
        this.filler = Optional.of(filler);
    }

    /**
     * Fills the first possible placeholder operand with fill
     *
     * @param fill the 'filler' attribute of a child tile
     *
     * @throws AssertionError if fill is empty
     */
    protected void incorporateFiller(AssemblyOperand fill) {
        assert !fill.isPlaceholder();
        for (AssemblyStatement stmt : statements) {
            if (stmt.hasPlaceholder())
                stmt.fillPlaceholder(fill.value());
        }
    }

    /**
     * incorporates all fillers and statements from all children into
     * this instance
     *
     * @param childAssemblies the assemblies of child tiles
     */
    public void merge(boolean childrenFirst, Assembly... childAssemblies) {
        // incorporate all child fillers
        Arrays.stream(childAssemblies).forEachOrdered(
                child -> child.filler.ifPresent(fill -> incorporateFiller(fill))
        );

        ArrayList<AssemblyStatement> childStatements = new ArrayList<>();
        Arrays.stream(childAssemblies).forEachOrdered(
                child -> childStatements.addAll(child.statements)
        );

        if (childrenFirst)
            statements.addAll(0, childStatements);
        else
            statements.addAll(statements.size(), childStatements);

    }

    /**
     * a unit of assembly is incomplete if any statements have empty
     * placeholder operands, which were should have been filled by a child
     */
    public boolean incomplete() {
        for (AssemblyStatement stmt : statements)
            if (stmt.hasPlaceholder()) return true;
        return false;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (AssemblyStatement stmt : statements) {
            s.append(stmt);
            s.append(System.getProperty("line.separator"));
        }
        return s.toString();
    }
    
    /**
     * top function for register allocation
     * @return real assembly elimiating all temp
     */
    public Assembly registerAlloc() {
    		rTable = new RegisterTable();
    		rTable.SetCounter(0);		// set the counter which decides the position of the first spilled location on the stack
    		// if the initial counter is 0, the ith register will be spilled to [rbp - 8 * i]
    
        LinkedList<AssemblyStatement> concreteStatements = new LinkedList<>();
       
    		for (AssemblyStatement stmt: statements) {
    			// two-pass process
    			// PASS 1: establish RegisterTable
    			// PASS 2: replace register with respective stack location
    			AssemblyStatement keyStatement = stmt;
    			List<Integer> opMemIndex = new ArrayList<Integer>();
    			
    			// PASS 1: check the register and allocate address on stack
    		    for (AssemblyOperand op: stmt.operands ) {
    		    	
    		    	    // Resolve the type of the operand in case it is not resolved at initialization
    		    	
    		    		op.ResolveType();
    		    		// check if op is temp or contains temp, e.g., move[__FreshTemp_14], 116
    		    		if (op.type == AssemblyOperand.OperandType.MEM || op.type == AssemblyOperand.OperandType.TEMP) {
    		    			
    		    	         if ( op.type == AssemblyOperand.OperandType.TEMP) {
    		    			// check if the temp is registered in the table, if not add it
    		    	        	 	if (!rTable.isInTable(op.operand)) {
    		    	        	 		rTable.add(op.operand);
    		    	        	 	}
    		    	        	 	opMemIndex.add(rTable.MemIndex(op.operand));
    		    	         } else if ( op.type == AssemblyOperand.OperandType.MEM) {
    		    	        	     String reg = op.operand.substring(1, op.operand.length()-1);
    		    	        	     if ( !rTable.isInTable(reg)) {
        		    	        	     System.out.println(reg);
    		    	        	    	     rTable.add(reg);
    		    	        	     } else {
    		    	        	    	 	System.out.print(rTable.MemIndex(reg));
    		    	        	     }
    		    	        	     opMemIndex.add(rTable.MemIndex(reg));
    		    	         }
    		    		} else {
    		    			opMemIndex.add(-1);
    		    		}
    		    }
    		    
    		    // PASS 2: replace the register with the respective address on stack
    		    // currently only support at least two registers in one instruction TODO: support >=3 registers in one instruction
    		    for ( int j = 0; j <opMemIndex.size(); j++) {
    		    		if ( j >= 2) break;
    		    	
    		    		if (opMemIndex.get(j) == -1) {
    		    			continue;
    		    		}
    		    		if (stmt.operands[j].type == AssemblyOperand.OperandType.TEMP) {
    		    			//   TODO figure out elegant way handling temp register inside operand
    		    			if ( j == 0 ) {
    		    				keyStatement.operands[j] = new AssemblyOperand("rax");
    		    			} else {
    		    				keyStatement.operands[j] = new AssemblyOperand("rdx");
    		    			}
    		    		} else if ( stmt.operands[j].type == AssemblyOperand.OperandType.MEM) {
    		    			// TODO
    		    			if ( j == 0 ) {
    		    				keyStatement.operands[j] = new AssemblyOperand("[rax]");
    		    			} else {
    		    				keyStatement.operands[j] = new AssemblyOperand("[rdx]");
    		    			}
    		    		}
    		    			
    		    }
 
    		    // append the newly generated statement (3 STEPS0
    		    // STEP 1: load two operands from memory
    		    if (opMemIndex.size() > 1 && opMemIndex.get(1)!= -1) {
    		       		AssemblyStatement loadMemStatement =new AssemblyStatement(
									"mov", 
									new AssemblyOperand("rdx"), 
									new AssemblyOperand("[rbp-" +String.valueOf(8 * opMemIndex.get(1))+ "]") );
		    		    concreteStatements.add(loadMemStatement);
    		    }
    		    //TODO depends on the type of actual operation, (e.g. mov), this step maybe omitted, but for (add, sub) need to preserve
    		    if (opMemIndex.size() > 0 && opMemIndex.get(0) != -1 ) {
        			AssemblyStatement storeMemStatement = new AssemblyStatement(
							"mov",
							new AssemblyOperand("rax"),
							new AssemblyOperand("[rbp-"+String.valueOf(8* opMemIndex.get(0))+"]")
							);
    		    		concreteStatements.add(storeMemStatement);
    		    }
    		    // STEP 2: key operation
    		    	concreteStatements.add(keyStatement);
    		    	//STEP 3: load the result back to memory (if the first operands of keyStatement involve register)
    		    if (opMemIndex.size() > 0 && opMemIndex.get(0) != -1 ) {
        			AssemblyStatement storeMemStatement = new AssemblyStatement(
							"mov",
							new AssemblyOperand("[rbp-"+String.valueOf(8* opMemIndex.get(0))+"]"), 
							new AssemblyOperand("rax"));
    		    		concreteStatements.add(storeMemStatement);
    		    }
    		}
    		return new Assembly(concreteStatements);
    }
}
