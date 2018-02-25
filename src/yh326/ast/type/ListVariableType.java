/**
 * 
 */
package yh326.ast.type;

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
    
    public ListVariableType(VariableType... variableTypes) {
        this.variableTypes = new ArrayList<VariableType>();
        for (VariableType variableType : variableTypes) {
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
