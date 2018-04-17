package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class AssemblyWrapper {
    public static Assembly GenerateAssembly(Tile tile, String outputFile) {
        Assembly assm = tile.generateAssembly();
        
        // For internal usage.
        try {
            if (Settings.genAbstract) { // write abstract assembly
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile + ".aasm")));
                writer.write(assm.toString());
                writer.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (!Settings.disAsmGen)
            assm = assm.registerAlloc();
        
        if (assm.incomplete()) {
            System.out.println("Incomplete assembly code!:");
            System.out.println(assm.toString());
            return assm;
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
            writer.write(assm.toString());
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return assm;
    }
}
