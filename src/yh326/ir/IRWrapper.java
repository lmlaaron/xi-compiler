package yh326.ir;

import java.io.FileWriter;
import java.nio.file.Paths;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.ast.node.Node;
import yh326.exception.XiException;
import yh326.typecheck.TypecheckerWrapper;

public class IRWrapper {

	public static void IRGeneration(String realInputFile, String realOutputDir, 
			String fileName, String libPath, boolean optimization) {
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".ir";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        try {
			FileWriter writer = new FileWriter(realOutputFile);
            try {
                Node ast = TypecheckerWrapper.getTypechecked(realInputFile, libPath);
                ast.fileName = fileName.substring(0, fileName.lastIndexOf("."));
                // Design not finished
	            IRNode irNode = ast.translateProgram();
	            writer.write(irNode.toString());
            } catch (XiException e) {
                writer.write(e.getMessage() + "\n");
            }
            writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return;
	}
	
	// Design not finished
	public static void IRRun() {
		
	}
}
