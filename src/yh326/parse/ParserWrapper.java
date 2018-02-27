package yh326.parse;

import java.io.FileReader;
import java.io.FileWriter;

import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class ParserWrapper {
	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	/**
	 * 
	 * @param absolute_file_path, an absolute path to the input file
	 * @param dest_path, an absolute path to the output directory
	 */
	public static void Parsing(String absolute_file_path, String dest_path) {
		
		// generate the output postfix
        String postfix = absolute_file_path.substring(absolute_file_path.lastIndexOf(".") + 1);
        String out_postfix=".parsed";
		if (postfix.equals("ixi")) out_postfix=".iparsed";
		else if (postfix.equals("xi")) out_postfix=".parsed";
		else {
			System.out.println("file postfix isn't correct");
			System.exit(1);
		}
		
		// generate the complete output path
		String absolute_output_path = 
				dest_path + 
				"/" + 
				absolute_file_path.substring(absolute_file_path.lastIndexOf("/") + 1, absolute_file_path.lastIndexOf(".")) + out_postfix;
		
		// parse
		try {
		    FileWriter writer = new FileWriter(absolute_output_path);
            lexer x = new lexer(new FileReader(absolute_file_path));
			@SuppressWarnings("deprecation")
            parser p = new parser(x);
			try {
			    write((Node) p.parse().value, writer);
			} catch (ParsingException e) {
			    writer.write(e.getMessage() + "\n");
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
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
