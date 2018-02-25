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
public class VariableType extends NodeType {
    
    private Primitives type;
    private int level;
    private List<Expr> sizes;
    
    /**
     * @param type
     */
    public VariableType(Primitives type) {
        this.type = type;
        this.level = 0;
        this.sizes = new ArrayList<Expr>();
    }
   
    /**
     * @return
     */
    public Primitives getType() {
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
    
    public boolean decreaseLevel() {
        if (level > 0) {
            level--;
            sizes.remove(0);
            return true;
        } else {
            return false;
        }
    }
    
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public String toString() {
        String typeString = type == Primitives.INT ? "int" : "bool";
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
        if (other instanceof VariableType) {
            VariableType otherType = (VariableType) other;
            return type == otherType.getType() && 
                   level == otherType.getLevel() ||
                   type == Primitives.ANY ||
                   otherType.getType() == Primitives.ANY;
        } else {
            return false;
        }
    }
    
}
