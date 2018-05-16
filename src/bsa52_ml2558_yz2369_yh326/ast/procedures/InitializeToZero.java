package bsa52_ml2558_yz2369_yh326.ast.procedures;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;

public class InitializeToZero {
    /**
     * Xi requires that all types be initialized to default values. They are as follows:
     *
     * int -> 0
     * bool -> false
     * any class -> null
     * array -> null
     *
     * We don't apply this default value in cases where a variable is assigned on the same
     * line it is declared (eg x : int = 2)
     *
     * @param ast
     * @return
     */
    public static Node do_it(Node ast) {
        // TODO:
        return ast;
    }
}
