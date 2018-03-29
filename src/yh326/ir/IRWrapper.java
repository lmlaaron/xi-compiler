package yh326.ir;

import java.io.FileWriter;
import java.util.List;

import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;
import yh326.ast.node.Node;
import yh326.util.IRFuncDeclFinder;

/**
 * Wrapper for IR generation, canonicalization, and constant folding
 * 
 * @author lmlaaron
 *
 */
public class IRWrapper {

    /**
     * Generate the IR file
     */
    public static void WriteIRResult(IRNode irNode, String realOutputFile) {
        try {
            FileWriter writer = new FileWriter(realOutputFile);
            writer.write(irNode.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /*
     * for testing purposes, will run both an optimized and non-optimized version of
     * the generated IR code
     */
    public static void IRRun(IRNode irNode) {
        List<IRFuncDecl> functions = findFuncDecls(irNode);

        if (functions.stream().noneMatch(decl -> (decl.name().equals("_Imain_paai")))) {
            System.out.println("Can't run this file -- no _Imain_paai function found.");
            System.out.println("That file contains the following functions:");
            functions.stream().forEach(decl -> System.out.println('\t' + decl.name()));
            return;
        }

        IRSimulator sim = new IRSimulator((IRCompUnit) irNode);
        sim.call("_Imain_paai"); // don't care about return value

    }

    private static List<IRFuncDecl> findFuncDecls(IRNode root) {
        IRFuncDeclFinder funcFinder = new IRFuncDeclFinder();
        root.visitChildren(funcFinder);
        return funcFinder.getFuncDecls();
    }

    /**
     * @param ast
     *            Type-checked AST node
     * @param realInputFile
     * @param fileName
     * @param libPath
     * @param optimization
     * @return
     * @throws Exception
     */
    public static IRNode IRGeneration(Node ast, String realInputFile, boolean optimization) throws Exception {
        IRNode irNode = ast.translateProgram();
        // System.out.println(irNode.toString());
        irNode = Canonicalization.Canonicalize(irNode);
        irNode = Canonicalization.Lift(irNode);
        irNode = Canonicalization.BlockReordering(irNode);
        irNode = Canonicalization.TameCjump(irNode);
        if (optimization) {
            irNode = Canonicalization.Folding(irNode);
        }
        // System.out.println(irNode.toString());
        return irNode;
    }
}
