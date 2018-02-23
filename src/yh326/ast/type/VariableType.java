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
