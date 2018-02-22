package yh326.ast.type;

import yh326.ast.exception.TypeErrorException;

/**
 * @author Syugen
 *
 */
/**
 * @author Syugen
 *
 */
public class VarType extends Type {
    
    private PrimitiveType type;
    private int level;
    
    /**
     * @param type
     */
    public VarType(PrimitiveType type) {
        this.type = type;
        this.level = 0;
    }
    
    /**
     * @param type
     * @param level
     */
    public VarType(PrimitiveType type, int level) {
        this.type = type;
        this.level = level;
    }
    
    
    /**
     * @param other
     * @return
     * @throws Exception 
     */
    public void checkType(VarType other) throws TypeErrorException {
        if (this.type != other.type || this.level != other.level) {
            throw new TypeErrorException(this, other);
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
    
    
}
