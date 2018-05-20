package bsa52_ml2558_yz2369_yh326.ast.node.classdecl;

import bsa52_ml2558_yz2369_yh326.util.graph.DirectedGraph;
import bsa52_ml2558_yz2369_yh326.util.graph.UndirectedGraph;

import java.util.*;

/**
 * A convenient global 'registry' to access class information. Whenever a
 * class is parsed, it's automatically added to this registry
 */
public class XiClasses {

    static HashSet<String> allClassNames;



    /**
     * The function name here is a misnomer, in reality we are trying to
     * perform static checks on the code to ensure invalid code gets rejected
     *
     * currently checks for:
     *
     * - two definitions of same class
     * - cyclic inheritance
     * - shared attribute name between parent and child in same module
     *
     */
    public static void postprocessIndices(List<XiClass> classes) {
        System.out.println(classes.size() + " classes:");
        for (XiClass xc : classes)
            System.out.println(xc.classId.value);

        verifyUnique(classes);

        Set<XiClass> roots = getRoots(classes);
        HashMap<XiClass, DirectedGraph<XiClass>> forest = getForest(roots, classes);

        for (XiClass root : roots) {
            validateTree(root, forest.get(root), new HashSet<>());
        }
    }

    private static void verifyUnique(List<XiClass> classes) {
        HashSet<String> names = new HashSet<>();

        for (XiClass xc : classes) {
            String s = xc.classId.value;

//            if (names.contains(s)) {
//                System.err.println("Semantic error: two definitions for class " + s);
//                System.exit(1);
//            }

            names.add(s);
        }
        allClassNames = names;
    }

    private static void validateTree(XiClass root, DirectedGraph<XiClass> tree, Set<String> attributes) {
        for (String var : root.vars_ordered) {
            if (attributes.contains(var)) {
                System.err.println("Error: Class " + root.classId.value + " contains field " + var + " which is present in a parent");
                System.exit(1);
            }
            attributes.add(var);
        }

//        if (tree == null) {
//            System.out.println("Tree");
//        }
//        if (root == null) {
//            System.out.println("Root");
//        }
//        if (tree.getSuccessors(root) == null) {
//            System.out.println("function!");
//        }

        for (XiClass child : tree.getSuccessors(root)) {
            validateTree(child, tree, new HashSet<>(attributes));
        }
    }

    private static Set<XiClass> getRoots(List<XiClass> classes) {
        Set<XiClass> ret = new HashSet<>();

        ListIterator<XiClass> it = classes.listIterator();
        while (it.hasNext()) {
            XiClass xc = it.next();
            if (xc.superClass == null) {
                ret.add(xc);
                it.remove();
            }
        }

        return ret;
    }

    private static HashMap<XiClass, DirectedGraph<XiClass>> getForest(Set<XiClass> roots, List<XiClass> classes) {
        HashMap<XiClass, DirectedGraph<XiClass>> forest = new HashMap<>();

        for (XiClass root : roots) {
            DirectedGraph<XiClass> dg = new DirectedGraph<XiClass>("graph");
            dg.addVertex(root);
            forest.put(root, dg);
        }

        while (!classes.isEmpty()) {
            int sizeBefore = classes.size();

            ListIterator<XiClass> it = classes.listIterator();
            while (it.hasNext()) {
                XiClass xc = it.next();

                if (forest.containsKey(xc.superClass)) {
                    forest.put(xc, forest.get(xc.superClass));
                    forest.get(xc).addEdge(xc.superClass, xc);

                    it.remove();
                }
            }

            if (sizeBefore == classes.size()) {
                System.err.println("Semantic Error: inheritance cycle");
                System.exit(1);
            }
        }
        return forest;
    }


}
