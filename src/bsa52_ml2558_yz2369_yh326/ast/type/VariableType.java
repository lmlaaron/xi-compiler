package bsa52_ml2558_yz2369_yh326.ast.type;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;

public abstract class VariableType extends NodeType {
    protected int level = 0;
    protected List<Expr> sizes = new ArrayList<Expr>();
    
    public VariableType() {}
    
    public VariableType(int level) {
        for (int i = 0; i < level; i++) {
            increaseLevel();
        }
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

    public abstract String toShortString();

    public abstract boolean isSubclassOf(NodeType other);


    public abstract VariableType copy();
}
