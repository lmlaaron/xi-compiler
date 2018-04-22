package bsa52_ml2558_yz2369_yh326.ir;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.optimization.cse.CommonSubexpressionElimination;
import bsa52_ml2558_yz2369_yh326.util.IRFuncDeclFinder;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import bsa52_ml2558_yz2369_yh326.util.TempRenamer;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;
import edu.cornell.cs.cs4120.xic.ir.interpret.IRSimulator;

/**
 * Wrapper for IR generation, canonicalization, and constant folding
 * 
 * @author lmlaaron
 *
 */
public class IRWrapper {

    /**
     * @param ast
     *            Type-checked AST node
     * @param fileName
     * @param libPath
     * @param optimization
     * @return
     * @throws Exception
     */
    public static IRNode IRGeneration(Node ast, String outputFile) throws Exception {
        IRNode irNode = ast.translateProgram();

        // add special dollar symbol to ensure if the variable
        // name has this suffix, it's as a result of this function,
        // and not just an original part of the variable name
        markTempNames(irNode, "_irtmp$");

        // System.out.println(irNode.toString());
        irNode = Canonicalization.Canonicalize(irNode);
        irNode = Canonicalization.Lift(irNode);
        irNode = Canonicalization.BlockReordering(irNode);
        irNode = Canonicalization.TameCjump(irNode);
        
        if (Settings.optCFGSet.contains("initial"))
            WriteDotResult(irNode, outputFile, "initial");
        
        if (Settings.opts.contains("cse")) {
            CommonSubexpressionElimination.DoCSE(irNode);
        }
        
        if (Settings.opts.contains("cf"))
            irNode = Canonicalization.Folding(irNode);
        if (Settings.optCFGSet.contains("cf"))
            WriteDotResult(irNode, outputFile, "cf");
        

        // System.out.println(irNode.toString());
        if (Settings.irgen) {
            WriteIRResult(irNode, outputFile + ".ir");
        }
        return irNode;
    }

    /**
     * Generate the IR file
     */
    public static void WriteIRResult(IRNode irNode, String outputFile) {
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(irNode.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    
    public static void WriteDotResult(IRNode irNode, String outputFile, String phase) {
        Map<String, IRFuncDecl> funcMap = ((IRCompUnit) irNode).functions();
        for (String name : funcMap.keySet()) {
            String dot = ControlFlowGraph.fromIRFuncDecl(funcMap.get(name)).toDotFormat();
            try {
                FileWriter writer = new FileWriter(outputFile + "_" + name + "_initial.dot");
                writer.write(dot);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            functions.stream().forEachOrdered(decl -> System.out.println('\t' + decl.name()));
            return;
        }

        IRSimulator sim = new IRSimulator((IRCompUnit) irNode);
        sim.call("_Imain_paai"); // don't care about return value

    }

    private static List<IRFuncDecl> findFuncDecls(IRNode root) {
        IRFuncDeclFinder funcFinder = new IRFuncDeclFinder();
        funcFinder.visit(root);
        return funcFinder.getFuncDecls();
    }

    /**
     * This step is added to avoid variable names such as "rax" causing trouble at
     * assembly level
     *
     * @param root
     *            the root of all IR code
     * @param suffix
     *            the suffix to append to all temp names
     */
    public static void markTempNames(IRNode root, String suffix) {
        TempRenamer renamer = new TempRenamer(suffix);
        renamer.visit(root);
    }

}
