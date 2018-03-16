package yh326.ast.type;

import java.util.ArrayList;
import java.util.List;

import yh326.ast.node.expr.Expr;
import yh326.ast.node.expr.ExprAtom;
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
   
    public VariableType(Primitives type, int level) {
        this.type = type;
        this.level = 0;
        this.sizes = new ArrayList<Expr>();
        for (int i = 0; i < level; i++) {
            increaseLevel();
        }
    }

    /**
     * @return
     */
    public Primitives getType() {
        return type;
    }

    public int getLevel() {
        return this.level;
    }
    
    public List<Expr> getSizes() {
        return this.sizes;
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
    
    public String toShortString() {
    		String result = "";
    		for (int i = 0; i < level; i++) {
    			result += "a";
    		}
    		switch (type) {
            case INT:   result += "i";   break;
            case BOOL:  result += "b";  break;
            default:    result += "?";     break;
        }
    		return result;
    }
    
    @Override
    public String toString() {
        String typeString = "";
        switch (type) {
        case INT:   typeString += "int";   break;
        case BOOL:  typeString += "bool";  break;
        default:    typeString += "?";     break;
        }
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
            if (type == otherType.getType() && level == otherType.getLevel()) {
                return true;
            } else if (type == Primitives.ANY || 
                    otherType.getType() == Primitives.ANY) {
                return true;
            } else if ((type == Primitives.EMPTY || 
                    otherType.getType() == Primitives.EMPTY) &&
                    level == otherType.getLevel()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
}
