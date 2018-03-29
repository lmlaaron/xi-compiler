package yh326.assembly;

import java.io.FileWriter;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.exception.XiException;
import yh326.ir.IRWrapper;

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
