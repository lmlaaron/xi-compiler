package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;

public class NewObject extends Expr {
    public NewObject(int line, int col, Expr dot) {
        super(line, col, new Keyword(line, col, "new"), dot);
    }  
}
