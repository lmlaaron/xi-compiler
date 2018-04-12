package bsa52_ml2558_yz2369_yh326.assembly;

import java.util.*;

import bsa52_ml2558_yz2369_yh326.exception.TileMergeException;


/**
Represents a block of assembly instructions, either complete or a fragment
introduced by a tile, which needs to be merged with child and parent assemblies.

When Assemblies are merged, it is not simple enough to prepend the lines from the
children before the lines of the parents. Often operations in a parent's statement
use arguments determined by one or more of the children. This is captured by the
filler attribute and merge() function.
 */
public class Assembly {
	
	final int MAX_PLACEHOLDER_INDEX = 5000; // 5000 arguments shold be enough for signle fucntion
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
	 * @throws TileMergeException if there is no place for the filler
     */
    protected void incorporateFiller(AssemblyOperand fill) throws TileMergeException {
        assert !fill.isPlaceholder();
        
        // find the placeholder with smallest value
        int min = MAX_PLACEHOLDER_INDEX;
       int index = -1;
        int counter = 0;
        for (AssemblyStatement stmt : statements) {
            if (stmt.hasPlaceholder()) {
            	//statements.get(index).fillPlaceholder(fill.value());
            	//return;
				if (stmt.getPlaceholder().reorderIndex < min) {
					min = stmt.getPlaceholder().reorderIndex;
					index = counter;
				}
			}   
            counter++;
        }
        
        //incorporate the filler
       // if ( min < MAX_PLACEHOLDER_INDEX)
       counter=0;
       for (AssemblyStatement stmt : statements) {
           if (stmt.hasPlaceholder() && counter == index) {
           		//stmt.fillPlaceholder(fill.value());
           		stmt.fillPlaceholder(fill); // copy all the properties of filler
        	   		return;
			}   
           counter++;
       }
        
        throw new TileMergeException("Assembly can't incorporate filler because there are no empty operands!");
    }

    /**
     * incorporates all fillers and statements from all children into
     * this instance
     *
     * @param childAssemblies the assemblies of child tiles
	 *
	 * @throws TileMergeException if the number of fillers in all children do not
	 * 							match the number of empty operands in the parent
     */
    public void merge(boolean childrenFirst, Assembly... childAssemblies) throws TileMergeException {
    		List<AssemblyOperand> operands = new ArrayList<AssemblyOperand>();
    	
    		// get all fillers from child in operands
		for (Assembly child : childAssemblies) {
			if (child.filler.isPresent()) {
				this.incorporateFiller(child.filler.get());
				//operands.add(child.filler.get());
			}
		}
		// sort the operands based on reorderIndex
		//Collections.sort(operands, new OperandComparator());
		
		//incorpoerate sorted filler onto list
		//for (AssemblyOperand opt : operands) {
		//	this.incorporateFiller(opt);
		//}
				//incorporateFiller(child.filler.get());

        ArrayList<AssemblyStatement> childStatements = new ArrayList<>();
        Arrays.stream(childAssemblies).forEachOrdered(
                child -> childStatements.addAll(child.statements)
        );

        if (childrenFirst)
            statements.addAll(0, childStatements);
        else
            statements.addAll(statements.size(), childStatements);

        for (AssemblyStatement stmt : statements)
        	if (stmt.hasPlaceholder())
        		throw new TileMergeException("Child assemblies did not fill all gaps in parent!");
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
    
    // per ABI specification, calculate the return size of a function
    static public int getRetSize(String targetName) {
		if ( targetName.equals("_xi_out_of_bounds")) {
			return 0;
		} else if ( targetName.equals("_xi_alloc")) {
			return 1;
		}
		try {
		if (targetName != null) {
			int index = targetName.lastIndexOf("t");
			if ( index != -1) {	// assume less than 100 arguments
				if ( targetName.toCharArray()[(index+1)]== 'p') {
					return 0;
				} else if (targetName.toCharArray()[(index+2)]!= 'a' && 
						targetName.toCharArray()[(index+2)]!= 'b' && 
						targetName.toCharArray()[(index+2)]!= 'i'  ) {
						String v = targetName.substring(index+1, index+3);
						return Integer.parseInt(v);
				} else {
					return Integer.parseInt(targetName.substring(index+1,index+2));
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
    
    // per ABI specification, calculate the argument size
    static public int getArgSize(String targetName) {
		if ( targetName.equals("_xi_out_of_bounds")) {
			return 0;
		} else if ( targetName.equals("_xi_alloc")) {
			return 1;
		}
		try {
    		if ( targetName != null) {
    			int index = targetName.lastIndexOf("_");
    			String sigStr = targetName.substring(index+1);
    			int num_a = 0;
    			int num_ib = 0;
    			for ( char s: sigStr.toCharArray()) {
    				if ( s == 'a') {
    					num_a++;
    				} else if ( s == 'i' || s == 'b') {
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
   
    // translate _ARG0, _RET0 to register or stack location
    static public String ARGRET2Reg(String name, int argc) {
    		//System.out.println(name);
    		//System.out.println(name.substring(0,4));
    		//System.out.println("_ARG".length());
    		if ( name != null && name.length() >= "_ARG".length() && name.substring(0, 4).equals("_ARG")) {
    			int v = Integer.valueOf(name.substring(4));
    			switch (v) {
    			case 0: return "rdi";
    			case 1: return "rsi";
    			case 2: return "rdx";
    			case 3: return "rcx";
    			case 4: return "r8";
    			case 5: return "r9";
    			default: return "[rbp+"+ String.valueOf((v-6+2)*8)+"]";		// rbp from callee point of view
    			}
    		} else if (name != null && name.length() >= "_RET".length() && name.substring(0,4).equals( "_RET")) {
    			int v = Integer.valueOf(name.substring(4));
    			switch (v) {
    			case 0: return "rax";
    			case 1: return "rdx";
    			default: if ( argc > 6) return "QWORD PTR [rsp+"+String.valueOf(((v-2)+1+(argc-6))*8)+"]"; else return "[rsp+" +String.valueOf((v-2)*8)+ "]";
    			// rsp from caller pointer of view
    			}
    		}
    		return name;
    }
    
    /**
     * top function for register allocation
     * @return real assembly elimiating all temp
     */
    public Assembly registerAlloc() {
        LinkedList<AssemblyStatement> concreteStatements = new LinkedList<>();
        List<List<AssemblyStatement>> ListFuncStatements = new LinkedList<>();
        
        // the register allocation/spilling is based on the unit of functions,
        //we assume the entire abstract assembly is seperated by function call labels
   	   List<AssemblyStatement> FuncStatements = new LinkedList<>();
   	   //int sss =0;
       for (AssemblyStatement stmt: statements) {
			if (stmt.isFunctionLabel && stmt.operation.substring(0,2).equals("_I"))  { // per Xi ABI specification, the function labels must start with ``_I'', we assume that vice versa
				//sss++;
				//System.out.println(stmt.toString()+"\n");
				ListFuncStatements.add(FuncStatements);
				FuncStatements = new LinkedList<>();
				FuncStatements.add(stmt);
//				System.out.println("Label: " + stmt);
			}
			else {
				FuncStatements.add(stmt);
			}
       }
       ListFuncStatements.add(FuncStatements);


//       // Debug printing:
//		int i = 1;
//		for (List<AssemblyStatement> func : ListFuncStatements) {
////			System.out.println("Function " + i);
//			for (AssemblyStatement statement : func) {
//				System.out.println(statement);
//			}
//			System.out.println();
//		}

//		System.out.println("====================================");
//		System.out.println();

       //System.out.printf("sss "+String.valueOf(sss)+"\n");
       
       for (List<AssemblyStatement> oneFuncStatements: ListFuncStatements) {
//       		System.out.println("Func Label : " + oneFuncStatements.get(0));

			int thisFuncArgSize = 0;
			// two-pass process
			// PASS 1: establish RegisterTable
			// PASS 2: replace register with respective stack location
    	   
      		rTable = new RegisterTable();
       		rTable.SetCounter(0);		// set the counter which decides the position of the first spilled location on the stack
   			int retSize = 0;
   			int argSize = 0;
   			int maxSize = 0;    		
   			
   			int lastCallArgc = 0;
       		for (AssemblyStatement stmt: oneFuncStatements) {
       			// find out the size of the allocated return space via the ABI
       			// the return statement in this function body needs this value to find the stack 
       			//pointer to store the return value (like [rbp+...]
       			if (stmt.isFunctionLabel  && stmt.operation.substring(0,2).equals("_I")) {
       				thisFuncArgSize = getArgSize(stmt.operation);
       			}
       			// calculate the stacksize
       			if (stmt.operation == "call") {
       				retSize = getRetSize(stmt.operands[0].value());
       				argSize = getArgSize(stmt.operands[0].value());
       				if ( retSize + argSize> maxSize) {
       					maxSize = retSize + argSize;
       				}
       				lastCallArgc = argSize;
       			}
       			
       			// Per IR specification, replace _ARG0, _RET0 etc with respective register
       			for (AssemblyOperand op: stmt.operands) {
       				op.ResolveType();

       				List<String> temps = op.getTemps();
       				ListIterator<String> it = temps.listIterator();
       				while (it.hasNext())
						it.set(ARGRET2Reg(it.next(), lastCallArgc));

       				op.setTemps(temps);
       			}
       			
       			// establish the registerTable
       			for (AssemblyOperand op: stmt.operands) {
       				op.ResolveType();
       				for (String temp : op.getTemps()) {
       					if (!rTable.isInTable(temp)) {
       						rTable.add(temp);
       						System.out.println("Register Table Adding " + temp);
						}
					}
       			}
       		}

//		   System.out.println("Function after first pass:");
//       		for (AssemblyStatement statement : oneFuncStatements) {
//       			System.out.println(statement);
//			}
//			System.out.println();

       		for (AssemblyStatement stmt: oneFuncStatements) {

       			// replace STACKSIZE with the real size
       			if (stmt.operands != null &&  stmt.operation.equals("sub") && stmt.operands[1].value().equals( "STACKSIZE")) {
       				//System.out.println("rTable.size()"+ String.valueOf(rTable.size()));
       				// calculate the size (pad if not 16byte aligned)
       				int stacksize = rTable.size() + maxSize;
       				if (stacksize % 2 != 0 ) { stacksize++; }
       				
       				stmt.operands[1] = new AssemblyOperand(String.valueOf(stacksize*8));
       				concreteStatements.add(stmt);

       				continue;
       			}
       			// replace __RETURN_x (genereated using return tile) with the exact stack location
       			if (stmt.operands != null && stmt.operation.equals("mov") && stmt.operands[0].type.equals(AssemblyOperand.OperandType.RET_UNRESOLVED) ) {
       				int index = stmt.operands[0].operand.lastIndexOf("_");
       				int offset = Integer.valueOf(stmt.operands[0].operand.substring(index+1));
       				AssemblyOperand  retOpt = null;
       				if (thisFuncArgSize <=6 ) {
       					retOpt = new AssemblyOperand("QWORD PTR [rbp+"+String.valueOf((2+ offset-2)*8)+"]");
       				} else {
       					retOpt = new AssemblyOperand("QWORD PTR [rbp+"+String.valueOf((2+ offset-2+ thisFuncArgSize-6)*8)+"]");
       				}
       				retOpt.type = AssemblyOperand.OperandType.REG_RESOLVED;
       				stmt.operands[0] = retOpt;
       				//concreteStatements.add(stmt);
       				//continue;
       			}

       			// if the initial counter is 0, the ith register will be spilled to [rbp - 8 * i]

				// TODO: support more than three registers
				LinkedList<String> availableRegisters = new LinkedList<>();
				availableRegisters.add("rbx");
				//availableRegisters.add("rcx");
				availableRegisters.add("rdx");

				LinkedList<AssemblyStatement> loadStatements = new LinkedList<AssemblyStatement>();
				LinkedList<AssemblyStatement> saveStatements = new LinkedList<AssemblyStatement>();

				// REGISTER ALLOCATION FOR THIS STATEMENT
           		for (int op_i = 0; op_i < stmt.operands.length; op_i++) {
           			AssemblyOperand op = stmt.operands[op_i];

           			op.ResolveType(); // just in case it wasn't resolved

					List<String> tempReplacements = new LinkedList<String>();
					List<String> temps = op.getTemps();
					for (String temp : temps) {
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

			}

   		
   		
       }
		return new Assembly(concreteStatements);
    }
}
