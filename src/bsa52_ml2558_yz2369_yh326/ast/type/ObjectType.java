package bsa52_ml2558_yz2369_yh326.ast.type;

import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.ExprAtom;

public class ObjectType extends VariableType {
    private XiClass type;

    /**
     * @param type
     */
    public ObjectType(XiClass type) {
        super();
        this.type = type;
    }

    public ObjectType(XiClass type, int level) {
        super(level);
        this.type = type;
    }

    /**
     * @return
     */
    public XiClass getType() {
        return type;
    }
    
    @Override
    public String toShortString() {
        String result = "";
        for (int i = 0; i < level; i++) {
            result += "a";
        }
        result += "o" + String.valueOf(type.classId.value.length()) + type.classId.value;
        return result;
    }

    @Override
    public String toString() {
        String typeString = type.classId.value;
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
    public boolean isSubclassOf(NodeType other) {
        if (other instanceof ObjectType) {
            ObjectType otherType = (ObjectType) other;
            XiClass cur = type;
            while (cur != null) {
                if (cur.classId.value.equals(otherType.type.classId.value)) {
                    return true;
                }
                cur = cur.super_class;
            }
            return false;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean equals(Object other) {
        // Note that sizes are not checked!
        if (other instanceof PrimitiveType) {
            PrimitiveType otherType = (PrimitiveType) other;
            if (otherType.getType() == Primitives.ANY) {
                return true;
            } else if (otherType.getType() == Primitives.EMPTY && level == otherType.getLevel()) {
                return true;
            } else {
                return false;
            }
        } else if (other instanceof ObjectType) {
            ObjectType otherType = (ObjectType) other;
            if (type == otherType.getType() && level == otherType.level) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * translate string of the variable name to index 
     */
    // Should field name be a more proper name?
    public int indexOfVar(String varname) {
    		return type.indexOfVar(varname);
    }
    
    /**
     * translate string of function name to index 
     */
    public int indexOfFunc(String funcname) {
    		return type.indexOfFunc(funcname);
    }
}
