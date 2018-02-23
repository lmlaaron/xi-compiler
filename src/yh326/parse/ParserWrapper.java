package yh326.parse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;

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

	public static void Parsing(String source_path, String dest_path, String file_path) {
		
		// generate the output postfix
        String postfix = file_path.substring(file_path.lastIndexOf(".") + 1);
		//PostfixType type;
        String out_postfix=".parsed";
		if (postfix.equals("ixi")) {
			//type = PostfixType.INTERFACE;
            out_postfix=".iparsed";
		}
		else if (postfix.equals("xi")) {
			//type = PostfixType.REGULAR;
            out_postfix=".parsed";
		}
		else {
			System.out.println("file postfix isn't correct");
			System.exit(1);
		}
		
		// generate the complete input and output path
		String absolute_file_path;
		String absolute_output_path;
		File temp = new File(file_path);
		if (temp.isAbsolute()) {
			absolute_file_path = file_path;
		}
		else {
			absolute_file_path = source_path + "/" + file_path;
		}
		absolute_output_path = dest_path + "/" + file_path.substring(file_path.lastIndexOf("/") + 1, file_path.lastIndexOf(".")) + out_postfix;
		
		// parse
		try {
		    FileWriter writer = new FileWriter(absolute_output_path);
            lexer x = new lexer(new FileReader(absolute_file_path));
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
