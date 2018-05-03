/**
 * 
 */
package bsa52_ml2558_yz2369_yh326.ast.type;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Syugen
 *
 */
public class ListVariableType extends NodeType {
    private List<VariableType> variableTypes;

    public ListVariableType(List<VariableType> variableTypes) {
        this.variableTypes = variableTypes;
    }

    public ListVariableType(PrimitiveType... variableTypes) {
        this.variableTypes = new ArrayList<>();
        for (PrimitiveType variableType : variableTypes) {
            this.variableTypes.add(variableType);
        }
    }

    /**
     * @return
     */
    public List<VariableType> getVariableTypes() {
        return variableTypes;
    }

    @Override
    public String toString() {
        if (variableTypes.size() == 0) {
            return "unit";
        }
        String rs = "(" + variableTypes.get(0);
        for (int i = 1; i < variableTypes.size(); i++) {
            rs += ", " + variableTypes.get(i);
        }
        return rs + ")";
    }
    
    public boolean isSubclassOf(NodeType other) {
        if (other instanceof ListVariableType) {
            List<VariableType> otherTypes = ((ListVariableType) other).getVariableTypes();
            if (variableTypes.size() != otherTypes.size()) {
                return false;
            }
            for (int i = 0; i < variableTypes.size(); i++) {
                if (!variableTypes.get(i).isSubclassOf(otherTypes.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object other) {
        // Note that sizes are not checked!
        if (other instanceof ListVariableType) {
            List<VariableType> otherTypes = ((ListVariableType) other).getVariableTypes();
            if (variableTypes.size() != otherTypes.size()) {
                return false;
            }
            for (int i = 0; i < variableTypes.size(); i++) {
                if (!variableTypes.get(i).equals(otherTypes.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
