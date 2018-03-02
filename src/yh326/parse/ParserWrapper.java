package yh326.parse;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class ParserWrapper {
	/**
	 * 
	 * @param realInputFile, an absolute path to the input file
	 * @param realOutputDir, an absolute path to the output directory
	 */
	public static void Parsing(String realInputFile, String realOutputDir, String fileName) {
		// generate the output postfix
        String postfix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String outPostfix = postfix.equals("xi") ? ".parsed" : ".iparsed";
		
		// generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + outPostfix;
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
		// parse
		try {
		    lexer x = new lexer(new FileReader(realInputFile));
		    FileWriter writer = new FileWriter(realOutputFile);
            @SuppressWarnings("deprecation")
            parser p = new parser(x);
			try {
			    write((Node) p.parse().value, writer);
			} catch (ParsingException e) {
			    writer.write(e.getMessage() + "\n");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
     * Print the AST to System.out.
     * @param node The root of the AST to be printed.
     */
    public static void write(Node node, FileWriter fileWriter) {
        OptimalCodeWriter writer = new OptimalCodeWriter(fileWriter, 40);
        SExpPrinter printer = new CodeWriterSExpPrinter(writer);
        writeRec(node, printer);
        printer.close();
    }

    /**
     * Helper function of write(Node).
     * @param node The root of the AST to be printed.
     * @param printer The printer.
     */
    private static void writeRec(Node node, SExpPrinter printer) {
        if (node == null) {
            printer.startList();
            printer.endList();
        } else if (node.children == null) {
            printer.printAtom(node.value);
        } else {
            printer.startList();
            for (Node child : node.children) {
                writeRec(child, printer);
            }
            printer.endList();
        }
    }
}
