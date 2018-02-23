/**
 * 
 */
package yh326.ast.type;

import java.util.List;

/**
 * @author Syugen
 *
 */
public class FunctionType extends Type {
    private List<VariableType> args;
    private List<VariableType> rets;
    
    /**
     * @param args
     * @param rets
     */
    public FunctionType(List<VariableType> args, List<VariableType> rets) {
        this.args = args;
        this.rets = rets;
    }

    /**
     * @return
     */
    public List<VariableType> getArgs() {
        return args;
    }

    /**
     * @return
     */
    public List<VariableType> getRets() {
        return rets;
    }
}
