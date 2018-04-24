package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline.OptimizedRegisterAllocationPipeline;
import bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline.SpillAllTempsPipeLine;
import bsa52_ml2558_yz2369_yh326.ir.Canonicalization;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import bsa52_ml2558_yz2369_yh326.util.graph.ControlFlowGraph;
import edu.cornell.cs.cs4120.xic.ir.IRCompUnit;
import edu.cornell.cs.cs4120.xic.ir.IRFuncDecl;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class AssemblyWrapper {
    public static Assembly GenerateAssembly(Tile tile, String outputFile) {
        // Generate abstract assembly
        Assembly abstractAssm = tile.generateAssembly();
        if (Settings.genAbstract)
            WriteAssemblyResult(abstractAssm, outputFile + ".aasm");

        // Generate assembly
        Assembly assm;
        if (!Settings.disAsmGen) {
            if (Settings.brentHack)
                assm = new OptimizedRegisterAllocationPipeline().process(abstractAssm);
            else
                assm = new SpillAllTempsPipeLine().process(abstractAssm);
        } else assm = abstractAssm;
        
        if (assm.incomplete()) {
            System.out.println("Incomplete assembly code!:");
            System.out.println(assm.toString());
            return assm;
        }
        
        if (Settings.optCFGSet.contains("reg"))
            WriteDotResult(assm, outputFile, "reg");
        if (Settings.optCFGSet.contains("final"))
            WriteDotResult(assm, outputFile, "final");
        WriteAssemblyResult(assm, outputFile + (Settings.disAsmGen ? ".ra.s" : ".s"));
        return assm;
    }

    public static void WriteAssemblyResult(Assembly assm, String outputFile) {
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write("    .intel_syntax noprefix " + "\n");
            writer.write(assm.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void WriteDotResult(Assembly assm, String outputFile, String phase) {
        String dot = ControlFlowGraph.fromAssembly(assm).toDotFormat();
        try {
            FileWriter writer = new FileWriter(outputFile + "_FULL_" + phase + ".dot");
            writer.write(dot);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
