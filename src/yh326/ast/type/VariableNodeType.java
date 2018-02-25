package yh326.ast.type;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.node.expr.Expr;
import yh326.ast.node.expr.ExprAtom;
import yh326.exception.TypeErrorException;
/**
 * @author Syugen
 *
 */
public class VariableNodeType extends NodeType {
    
    private PrimitiveNodeType type;
    private int level;
    private List<Expr> sizes;
    
    /**
     * @param type
     */
    public VariableNodeType(PrimitiveNodeType type) {
        this.type = type;
        this.level = 0;
        this.sizes = new ArrayList<Expr>();
    }
   
    /**
     * @return
     */
    public PrimitiveNodeType getType() {
        return type;
    }

    public void increaseLevel() {
        level++;
        sizes.add(0, null);
    }
    
    /**
     * @param i
     */
    public void increaseLevel(Expr expr) {
        level++;
        sizes.add(0, expr);
    }
    
    public void decreaseLevel() {
        level--;
        sizes.remove(0);
    }
    
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public String toString() {
        String typeString = type == PrimitiveNodeType.INT ? "int" : "bool";
        for (int i = 0; i < level; i++) {
            typeString += "[";
            Expr sizeNode = sizes.get(i);
            if (sizeNode != null) {
                if (sizeNode instanceof ExprAtom) {
                    typeString += sizeNode.toString();
                } else {
                    typeString += "EXPR";
                }
            }
            typeString += "]";
        }
        return typeString;
    }

    @Override
    public boolean equals(Object other) {
        // Note that sizes are not checked!
        if (other instanceof VariableNodeType) {
            VariableNodeType otherType = (VariableNodeType) other;
            return otherType.getType() == getType() && 
                   otherType.getLevel() == getLevel();
        } else {
            return false;
        }
    }
    
}
