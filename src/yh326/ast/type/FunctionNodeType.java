/**
 * 
 */
package yh326.ast.type;

import java.util.List;

/**
 * @author Syugen
 *
 */
public class FunctionNodeType extends NodeType {
    private List<VariableNodeType> args;
    private List<VariableNodeType> rets;
    
    /**
     * @param args2
     * @param rets2
     */
    public FunctionNodeType(List<VariableNodeType> args2, List<VariableNodeType> rets2) {
        this.args = args2;
        this.rets = rets2;
    }

    /**
     * @return
     */
    public List<VariableNodeType> getArgs() {
        return args;
    }

    /**
     * @return
     */
    public List<VariableNodeType> getRets() {
        return rets;
    }
    
    @Override
    public String toString() {
        String rs = "(" + (args.size() == 0 ? "" : args.get(0));
        for (int i = 1; i < args.size(); i++) {
            rs += ", " + args.get(i);
        }
        rs += ") -> (" + (rets.size() == 0 ? "" : rets.get(0));
        for (int i = 1; i < rets.size(); i++) {
            rs += ", " + rets.get(i);
        }
        return rs + ")";
    }
}
