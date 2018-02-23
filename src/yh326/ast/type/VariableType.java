package yh326.ast.type;

/**
 * @author Syugen
 *
 */
public class VariableType extends Type {
    
    private PrimitiveType type;
    private int level;
    
    /**
     * @param type
     */
    public VariableType(PrimitiveType type) {
        this.type = type;
        this.level = 0;
    }
    
    /**
     * @param type
     * @param level
     */
    public VariableType(PrimitiveType type, int level) {
        this.type = type;
        this.level = level;
    }

    /**
     * @param other
     * @return unit type if a or b is unit type
     * @throws exception
     **/
    public Type Lub(Type a, Type b) throws TypeErrorException { 
        if ( a.equals(b)) {
           return a;
        } else if ( a instanceof varType ) { 
           if ( a.equals(varType(PrimitiveType.UNIT)) {
              return new varType(PrimitiveType.UNIT);
           } 
        } else if ( b instanceof varType ) {
           if ( b.equals(varType(PrimitiveType.UNIT)) {
              return new varType(PrimitiveType.UNIT);
           } 
        } else {
           throw new TypeErrorException(a, b);
        }
    }    
   
    /**
     * @return
     */
    public PrimitiveType getType() {
        return type;
    }

    /**
     * @return
     */
    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        String typeString = type == PrimitiveType.INT ? "int" : "bool";
        for (int i = 0; i < level; i++) {
            typeString += "[]";
        }
        return typeString;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableType) {
            VariableType otherType = (VariableType) other;
            return otherType.getType() == getType() && 
                   otherType.getLevel() == getLevel();
        } else {
            return false;
        }
    }
    
    
}
