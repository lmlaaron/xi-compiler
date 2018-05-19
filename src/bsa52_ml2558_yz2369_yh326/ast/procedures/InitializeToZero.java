package bsa52_ml2558_yz2369_yh326.ast.procedures;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.expr.Expr;
import bsa52_ml2558_yz2369_yh326.ast.node.literal.FalseLiteral;
import bsa52_ml2558_yz2369_yh326.ast.node.literal.IntegerLiteral;
import bsa52_ml2558_yz2369_yh326.ast.node.literal.NullLiteral;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Bracket;
import bsa52_ml2558_yz2369_yh326.ast.node.stmt.*;
import bsa52_ml2558_yz2369_yh326.ast.node.type.ArrayTypeNode;
import bsa52_ml2558_yz2369_yh326.ast.node.type.NonArrayTypeNode;
import bsa52_ml2558_yz2369_yh326.ast.node.type.TypeNode;
import bsa52_ml2558_yz2369_yh326.util.Settings;

import java.util.LinkedList;
import java.util.List;

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
        if (!Settings.defaultValues) return ast;

        // we iterate down subtrees starting from stmtlists because vardecls
        // can also occur in class definitions, where we are not allowed to replace them with
        // declarations with assignments
        LinkedList<StmtList> slists = new LinkedList<StmtList>();
        getToplevelStmtLists(ast, slists);
        for (StmtList slist : slists) {
            for (int i = 0; i < slist.children.size(); i++)
                visit(slist, slist.children.get(i), i);
        }
        return ast;
    }

    protected static void visit(Node parent, Node n, int n_i) {

        if (n instanceof AssignMult || n instanceof AssignSingle) {
            // because vardecls also exist inside of assignments,
            // we need to stop here
            return;
        }
        else if (n instanceof VarDecl) {
            // I'm assuming nested StmtLists are okay, and will canonicalize fine
            // ^^ actually no it doesn't work -- found another way
            //StmtList replacement = new StmtList(n.line, n.col);

            LinkedList<Node> stmts = new LinkedList<>();


            // get the rhs for each assignment! This value depends on the type of the variables
            Expr rhs = null; // default value depends on the type of the variable
            TypeNode type = (TypeNode)n.children.get(n.children.size()-1);
            if (type instanceof NonArrayTypeNode) { // primitives
                if (type.value.equals("int")) {
                    rhs = new IntegerLiteral(n.line, n.col, "0");
                } else if (type.value.equals("bool")) {
                    rhs = new FalseLiteral(n.line, n.col);
                }
                else {
                    // objects
                    rhs = new NullLiteral(n.line, n.col);
                }
            }
            else if (type instanceof ArrayTypeNode) { // arrays
                rhs = new NullLiteral(n.line, n.col);

                for (Node child : type.children){
                    if (!(child instanceof Bracket || child instanceof TypeNode)) {
                        rhs = null; // declaration such as `x : int[5]` match this structure (they are initialized!)
                    }
                }
            }
            else { // classes
                throw new RuntimeException("Bad Assumption!");
            }

            // add an assignment for each variable
            if (rhs != null) {
                for (int i = 0; i < n.children.size() - 1; i++) {
                    stmts.add(new AssignSingle(n.line, n.col, n.children.get(i), rhs));
                }
            }

            parent.children.addAll(n_i+1, stmts);

            // no such thing as nested vardecls:
            return;
        }

        // iterate through children
        if (n.children != null) {
            for (int i = 0; i < n.children.size(); i++) {
                visit(n, n.children.get(i), i);
            }
        }
    }

    protected static void getToplevelStmtLists(Node ast, List<StmtList> list) {
        if (ast instanceof StmtList)
            list.add((StmtList)ast);
        else if (ast.children != null) {
            for (Node child : ast.children) {
                if (child != null)
                    getToplevelStmtLists(child, list);
            }
        }
    }
}
