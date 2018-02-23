package yh326.ast.type;

import yh326.exception.TypeErrorException;
/**
 * @author Syugen
 *
 */
public class VariableNodeType extends NodeType {
    
    private PrimitiveNodeType type;
    private int level;
    
    /**
     * @param type
     */
    public VariableNodeType(PrimitiveNodeType type) {
        this.type = type;
        this.level = 0;
    }
    
    /**
     * @param type
     * @param level
     */
    public VariableNodeType(PrimitiveNodeType type, int level) {
        this.type = type;
        this.level = level;
    }

    /**
     * @param other
     * @return unit type if a or b is unit type
     * @throws exception
     **/
    public static NodeType Lub(NodeType a, NodeType b) throws TypeErrorException {
        if (a.equals(b)) {
            return a;
        } else if (a instanceof UnitNodeType) {
            return a;
        } else if (b instanceof UnitNodeType) {
            return b;
        } else {
            throw new TypeErrorException(a, b); // TODO Wrong use of the Exception
        }
    }    
   
    /**
     * @return
     */
    public PrimitiveNodeType getType() {
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
        String typeString = type == PrimitiveNodeType.INT ? "int" : "bool";
        for (int i = 0; i < level; i++) {
            typeString += "[]";
        }
        return typeString;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VariableNodeType) {
            VariableNodeType otherType = (VariableNodeType) other;
            return otherType.getType() == getType() && 
                   otherType.getLevel() == getLevel();
        } else {
            return false;
        }
    }
    
    
}
