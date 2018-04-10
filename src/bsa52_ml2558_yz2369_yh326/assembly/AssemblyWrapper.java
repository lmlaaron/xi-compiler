package bsa52_ml2558_yz2369_yh326.assembly;

import java.io.FileWriter;

public class AssemblyWrapper {
    public static void WriteAssemblyResult(Object TODO, String realOutputFile) {
        // generate the complete output path
        realOutputFile += ".s";

        try {
            FileWriter writer = new FileWriter(realOutputFile);
            try {
                // writer.write(irNode.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
