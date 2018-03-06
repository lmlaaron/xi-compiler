package yh326.typecheck;

import java.io.FileWriter;
import java.nio.file.Paths;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.exception.XiException;
import yh326.parse.ParserWrapper;

public class TypecheckerWrapper {
	/**
	 * 
	 * @param realInputFile, an absolute path to the input file
	 * @param realOutputDir, an absolute path to the output directory
	 */
	public static void Typechecking(String realInputFile, String realOutputDir, 
			String fileName, String libPath) {
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".typed";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        // typechecking
		try {
			FileWriter writer = new FileWriter(realOutputFile);
            try {
                getTypechecked(realInputFile, libPath);
	            writer.write("Valid Xi Program");
            } catch (XiException e) {
            		e.print(fileName);
            		writer.write(e.getMessage() + "\n");
            }
            writer.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return;
	}
	
	public static Node getTypechecked(String realInputFile, String libPath) throws Exception {
		Node ast = ParserWrapper.getParsed(realInputFile);
		SymbolTable sTable = new SymbolTable();
	    ast.loadMethods(sTable, libPath);
	    ast.typeCheck(sTable);
	    return ast;
	}
}
