/**
 * 
 */
package yh326.ast.type;

import java.util.List;

import yh326.ast.exception.TypeErrorException;
import yh326.ast.exception.WrongNumberOfArgumentsException;

/**
 * @author Syugen
 *
 */
public class FunctionType extends Type {
    private List<VarType> args;
    private List<VarType> rets;
    
    /**
     * @param args
     * @param rets
     */
    public FunctionType(List<VarType> args, List<VarType> rets) {
        this.args = args;
        this.rets = rets;
    }
    
    public void checkArguments(List<VarType> otherArgs) 
            throws WrongNumberOfArgumentsException, TypeErrorException {
        if (args.size() != otherArgs.size()) {
            throw new WrongNumberOfArgumentsException(args.size(), otherArgs.size());
        }
        for (int i = 0; i < args.size(); i++) {
            args.get(i).checkType(otherArgs.get(i));
        }
    }

    /**
     * @return
     */
    public List<VarType> getArgs() {
        return args;
    }

    /**
     * @return
     */
    public List<VarType> getRets() {
        return rets;
    }
}
