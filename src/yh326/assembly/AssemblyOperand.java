package yh326.assembly;

/**
 * represents a single operand of an assembly statement. This class
 * is helpful because some tile's AssemblyStatement instances will be missing
 * some operands that they expect to get from their children.
 */
public class AssemblyOperand {
    protected String operand;
    public enum OperandType {
    		TEMP, LABEL, MEM, CONSTANT, UNRESOLVED
    } 
    OperandType type;
    
    public AssemblyOperand(String op) {
        this.operand = op;
        this.type = OperandType.UNRESOLVED;
        this.ResolveType();
    }
    
    /**
     * resolve the type of operand by pattern matching
     */
    public void ResolveType() {
    	//TODO fix this pattern matching but propagate from IR instead
    
    	 if ( (this.operand.charAt(0)=='[' ) && (this.operand.charAt(this.operand.length()-1) ==']' ) ) {
    		    //	} else if (this.operand.contains("[")){
    		    		this.type = OperandType.MEM;
    		    		//System.out.println(this.operand);
    	} else if (this.operand.contains("__FreshTemp_") ||
    		this.operand.contains("_temp_") ||
    		this.operand.contains("_array_") ){
    		this.type = OperandType.TEMP;
    	} 
    //	this.type=OperandType.TEMP;
    	//TODO implement other types
    	
    		/*if (this.operand.contains("__FreshTemp_") ||
    				this.operand.contains("_temp_") ||
    				this.operand.contains("_array_") ||
    				this.operand.contains("_index_") ||
    				
    				) {
    			this.type = OperandType.TEMP;
    		} else if ( this.operand.contains("FreshLabel") ||
    				) {	
    		} else if () {
    			
    		} else if () {
    			
    		} else {
    			this.type = OperandType.UNRESOLVED;
    		}*/
    		
    }
    
    public AssemblyOperand() { }

    /**
     * @returns whether this operand expects to have its contents filled
     * by an external source
     */
    public boolean isPlaceholder() {
        return operand == null;
    }

    /**
     * @param operand the representation that this operand will assume
     */
    public void fillPlaceholder(String operand) {
        assert isPlaceholder();
        this.operand = operand;
    }

    /**
     * @return the representation of this operand
     */
    public String value() {
        return operand;
    }

    @Override
    public String toString() {
        if (isPlaceholder()) {
            return "_";
        }
        else {
            return value();
        }
    }
}
