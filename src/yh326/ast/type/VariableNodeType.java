package yh326.ast.type;

import yh326.ast.exception.*;
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
    public NodeType Lub(NodeType a, NodeType b) throws TypeErrorException {
        if ( a.equals(b)) {
           return a;
        } else if ( a instanceof VariableNodeType) {
           if ( a.equals(VariableType(PrimitiveNodeType.UNIT))) {
              return new VariableNodeType(PrimitiveNodeType.UNIT);
           } 
        } else if ( b instanceof VariableNodeType) {
           if ( b.equals(VariableType(PrimitiveNodeType.UNIT))) {
              return new VariableNodeType(PrimitiveNodeType.UNIT);
           } 
        } else {
           throw new TypeErrorException(a, b);
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
