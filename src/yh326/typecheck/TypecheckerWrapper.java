package yh326.typecheck;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.exception.SemanticCheckException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class TypecheckerWrapper {
	/**
	 * 
	 * @param realInputFile, an absolute path to the input file
	 * @param realOutputDir, an absolute path to the output directory
	 */
	public static void Typechecking(String realInputFile, String realOutputDir, String fileName, String libPath) {
		// Ignore .ixi files.
	    if (fileName.substring(fileName.lastIndexOf(".") + 1).equals("ixi")) {
		    return;
		}
        
        // generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".typed";
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
        // typechecking
		try {
		    lexer x = new lexer(new FileReader(realInputFile));
            FileWriter writer = new FileWriter(realOutputFile);
            @SuppressWarnings("deprecation")
            parser p = new parser(x);
			try {
			    Node ast = (Node) p.parse().value;
			    SymbolTable sTable = new SymbolTable();
			    ast.loadMethods(sTable, libPath);
			    ast.typeCheck(sTable);
	            writer.write("Valid Xi Program");
            } catch (ParsingException e) {
                writer.write(e.getMessage() + "\n");
            } catch (SemanticCheckException e) {
                writer.write(e.getMessage() + "\n");
            }
            writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
