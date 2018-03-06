package yh326.parse;

import java.io.FileWriter;
import java.nio.file.Paths;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.exception.XiException;
import yh326.gen.lexer;
import yh326.gen.parser;
import yh326.lex.LexerWrapper;

public class ParserWrapper {
	/**
	 * 
	 * @param realInputFile, an absolute path to the input file
	 * @param realOutputDir, an absolute path to the output directory
	 */
	public static void Parsing(String realInputFile, String realOutputDir, String fileName) {
		// generate the output postfix
        String postfix = realInputFile.substring(realInputFile.lastIndexOf(".") + 1);
        String outPostfix = postfix.equals("xi") ? ".parsed" : ".iparsed";
		
        // generate the complete output path
        String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + outPostfix;
        String realOutputFile = Paths.get(realOutputDir, outputFileName).toString(); 
        
		// parse
		try {
		    FileWriter writer = new FileWriter(realOutputFile);
		    try {
		    		Node ast = getParsed(realInputFile);
		    		write(ast, writer);
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
	
	public static Node getParsed(String realInputFile) throws Exception {
		lexer x = LexerWrapper.getLexer(realInputFile);
		@SuppressWarnings("deprecation")
        parser p = new parser(x);
        Node node = (Node) p.parse().value;
        String postfix = realInputFile.substring(realInputFile.lastIndexOf(".") + 1);
        if (postfix.equals("xi") && node.isInterface) {
        		throw new ParsingException(1, 1, "Expected Xi program, found Xi interface.\n");
        } else if (postfix.equals("ixi") && !node.isInterface) {
        		throw new ParsingException(1, 1, "Expected Xi interface, found Xi program.\n");
        }
        return node;
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
