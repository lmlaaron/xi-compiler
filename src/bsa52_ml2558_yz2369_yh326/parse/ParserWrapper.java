package bsa52_ml2558_yz2369_yh326.parse;

import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.gen.parser;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;

public class ParserWrapper {
    /**
     * 
     * @param realInputFile,
     *            an absolute path to the input file
     * @param realOutputDir,
     *            an absolute path to the output directory
     */
    public static void WriteParsingResult(Node ast, String realOutputFile) {
        try {
            FileWriter writer = new FileWriter(realOutputFile);
            write(ast, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * @param x
     *            lexer object or null
     * @param realInputFile
     * @return
     * @throws Exception
     */
    public static Node Parsing(lexer x, String realInputFile, String extension) throws Exception {
        @SuppressWarnings("deprecation")
        parser p = new parser(x);

        Node node = (Node) p.parse().value;
        if (extension.equals("xi") && node.isInterface) {
            throw new ParsingException(1, 1, "Expected Xi program, found Xi interface.\n");
        } else if (extension.equals("ixi") && !node.isInterface) {
            throw new ParsingException(1, 1, "Expected Xi interface, found Xi program.\n");
        }
        return node;
    }

    /**
     * Print the AST to System.out.
     * 
     * @param node
     *            The root of the AST to be printed.
     */
    public static void write(Node node, FileWriter fileWriter) {
        OptimalCodeWriter writer = new OptimalCodeWriter(fileWriter, 40);
        SExpPrinter printer = new CodeWriterSExpPrinter(writer);
        writeRec(node, printer);
        printer.close();
    }

    /**
     * Helper function of write(Node).
     * 
     * @param node
     *            The root of the AST to be printed.
     * @param printer
     *            The printer.
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
