package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline.OptimizedRegisterAllocationPipeline;
import bsa52_ml2558_yz2369_yh326.assembly.abstract_pipeline.SpillAllTempsPipeLine;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class AssemblyWrapper {
    public static Assembly GenerateAssembly(Tile tile, String outputFile) {
        Assembly abstractAssm = tile.generateAssembly();
        Assembly finalAssm = null;

        // For internal usage.
        try {
            if (Settings.genAbstract) { // write abstract assembly
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile + ".aasm")));
                writer.write(abstractAssm.toString());
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (!Settings.disAsmGen) {
            if (Settings.brentHack) {
                finalAssm = new OptimizedRegisterAllocationPipeline().process(abstractAssm);
            }
            else {
                finalAssm = new SpillAllTempsPipeLine().process(abstractAssm);
            }
        }
        else {
            finalAssm = abstractAssm;
        }

        if (finalAssm.incomplete()) {
            System.out.println("Incomplete assembly code!:");
            System.out.println(finalAssm.toString());
            return finalAssm;
        }

        // write assembly to file
        if (Settings.disAsmGen)
            outputFile += ".ra.s";
        else
            outputFile += ".s";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
            writer.write(".intel_syntax noprefix " + "\n");
            // intel syntax annotation
            writer.write(finalAssm.toString());
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return finalAssm;
    }


}
