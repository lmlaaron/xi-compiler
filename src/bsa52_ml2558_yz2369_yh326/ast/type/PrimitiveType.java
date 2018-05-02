package bsa52_ml2558_yz2369_yh326.ast.type;

import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;

/**
 * @author Syugen
 *
 */
public class PrimitiveType extends VariableType {

    private Primitives type;

    /**
     * @param type
     */
    public PrimitiveType(Primitives type) {
        super();
        this.type = type;
    }

    public PrimitiveType(Primitives type, int level) {
        super(level);
        this.type = type;
    }

    /**
     * @return
     */
    public Primitives getType() {
        return type;
    }

    @Override
    public String toShortString() {
        String result = "";
        for (int i = 0; i < level; i++) {
            result += "a";
        }
        switch (type) {
        case INT:
            result += "i";
            break;
        case BOOL:
            result += "b";
            break;
        default:
            result += "?";
            break;
        }
        return result;
    }

    @Override
    public String toString() {
        String typeString = "";
        switch (type) {
        case INT:
            typeString += "int";
            break;
        case BOOL:
            typeString += "bool";
            break;
        default:
            typeString += "?";
            break;
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
        if (other instanceof PrimitiveType) {
            PrimitiveType otherType = (PrimitiveType) other;
            if (type == otherType.getType() && level == otherType.getLevel()) {
                return true;
            } else if (type == Primitives.ANY || otherType.getType() == Primitives.ANY) {
                return true;
            } else if ((type == Primitives.EMPTY || otherType.getType() == Primitives.EMPTY)
                    && level == otherType.getLevel()) {
                return true;
            } else {
                return false;
            }
        } else if (other instanceof ObjectType) {
            if (type == Primitives.ANY) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
