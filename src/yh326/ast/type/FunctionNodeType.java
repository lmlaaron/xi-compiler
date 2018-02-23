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
     * @param args
     * @param rets
     */
    public FunctionNodeType(List<VariableNodeType> args, List<VariableNodeType> rets) {
        this.args = args;
        this.rets = rets;
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
}
