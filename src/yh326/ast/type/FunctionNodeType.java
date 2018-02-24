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
    private List<NodeType> args;
    private List<NodeType> rets;
    
    /**
     * @param args2
     * @param rets2
     */
    public FunctionNodeType(List<NodeType> args2, List<NodeType> rets2) {
        this.args = args2;
        this.rets = rets2;
    }

    /**
     * @return
     */
    public List<NodeType> getArgs() {
        return args;
    }

    /**
     * @return
     */
    public List<NodeType> getRets() {
        return rets;
    }
    
    @Override
    public String toString() {
        String rs = "(";
        for (NodeType node : args) {
            rs += node;
        }
        rs += ") -> (";
        for (NodeType node : rets) {
            rs += node;
        }
        return rs + ")";
    }
}
