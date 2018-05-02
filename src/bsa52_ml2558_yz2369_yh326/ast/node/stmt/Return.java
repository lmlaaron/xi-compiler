package bsa52_ml2558_yz2369_yh326.ast.node.stmt;

import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.ast.type.ListVariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.NodeType;
import bsa52_ml2558_yz2369_yh326.ast.type.UnitType;
import bsa52_ml2558_yz2369_yh326.ast.type.VariableType;
import bsa52_ml2558_yz2369_yh326.ast.type.VoidType;
import bsa52_ml2558_yz2369_yh326.exception.MatchTypeException;
import bsa52_ml2558_yz2369_yh326.exception.MismatchNumberException;
import edu.cornell.cs.cs4120.xic.ir.*;

public class Return extends Stmt {

    public Return(int line, int col) {
        super(line, col, new Keyword(line, col, "return"));
    }

    @Override
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        NodeType expected = sTable.getCurFuncReturnType();
        NodeType actual;

        // Nothing to be returned
        if (children.size() == 1) {
            actual = new UnitType();
        } else if (children.size() == 2) {
            actual = children.get(1).typeCheck(sTable);
            if (actual instanceof VariableType) {
                actual = (VariableType) actual;
            } else {
                throw new MatchTypeException(line, col, "int, bool, Object, or their array", actual);
            }
        } else {
            List<VariableType> listType = new ArrayList<>();
            for (int i = 1; i < children.size(); i++) {
                listType.add((VariableType) children.get(i).typeCheck(sTable));
            }
            actual = new ListVariableType(listType);
        }

        if (actual.getClass() != expected.getClass()) {
            throw new MatchTypeException(line, col, expected, actual);
        } else {
            // Procedure
            if (actual instanceof UnitType) {
                return new VoidType();
            } else if (actual instanceof VariableType) {
                if (((VariableType) actual).equals((VariableType) expected)) {
                    return new VoidType();
                } else {
                    throw new MatchTypeException(line, col, expected, actual);
                }
            } else { // ListVariableType
                ListVariableType act = (ListVariableType) actual;
                ListVariableType exp = (ListVariableType) expected;
                if (act.getVariableTypes().size() != exp.getVariableTypes().size()) {
                    throw new MismatchNumberException(line, col, exp.getVariableTypes().size(),
                            act.getVariableTypes().size());
                } else if (act.equals(exp)) {
                    return new VoidType();
                } else {
                    throw new MatchTypeException(line, col, expected, actual);
                }
            }
        }

    }

    @Override
    public IRNode translate() {
        // TODO: need to look into the structure of this return node
        if (children.size() == 1) {
            return new IRReturn(new ArrayList<IRExpr>());
        } else if (children.size() >= 2) {
            // translate retval children
            List<IRExpr> retvals = new ArrayList<>();
            for (int i = 1; i < children.size(); i++) {
                retvals.add((IRExpr) children.get(i).translate());
            }

            // assuming return takes care of assigning _RET0 ... _RETn-1 under the hood
            return new IRReturn(retvals);
        } else {
            // Should have thrown an exception during type check
            throw new RuntimeException("return node has zero children.");
        }
    }
}
