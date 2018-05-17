package bsa52_ml2558_yz2369_yh326.parse;

import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.gen.parser;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import edu.cornell.cs.cs4120.util.CodeWriterSExpPrinter;
import edu.cornell.cs.cs4120.util.SExpPrinter;
import polyglot.util.OptimalCodeWriter;

public class ParserWrapper {

    /**
     * @param x
     *            lexer object or null
     * @param inputFile
     * @return
     * @throws Exception
     */
    public static Node Parsing(lexer x, String outputFile, String extension) throws Exception {
        @SuppressWarnings("deprecation")
        parser p = new parser(x);

        Node node = (Node) p.parse().value;
        if (extension.equals(".parsed") && node.isInterface) {
            throw new ParsingException(1, 1, "Expected Xi program, found Xi interface.\n");
        } else if (extension.equals(".iparsed") && !node.isInterface) {
            throw new ParsingException(1, 1, "Expected Xi interface, found Xi program.\n");
        }
        if (Settings.parse) {
            ParserWrapper.WriteParsingResult(node, outputFile + extension);
        }
        return node;
    }

    public static void DebugPrintASTNodeTypes(Node ast) {
        _printASTTypes(ast, 0);
    }
    
    private static void _printASTTypes(Node n, int depth) {
        for (int i = 0; i < depth; i++)
            System.out.print("  ");
        System.out.print(n.getClass().getSimpleName());
        System.out.print(" ");
        System.out.println(n.value == null ? "" : n.value);
        if (n.children != null) {
            for (Node child : n.children) {
                if (child != null)
                    _printASTTypes(child, depth + 1);
            }
        }
    }

    /**
     * 
     * @param realInputFile,
     *            an absolute path to the input file
     * @param realOutputDir,
     *            an absolute path to the output directory
     */
    public static void WriteParsingResult(Node ast, String outputFile) {
        try {
            FileWriter writer = new FileWriter(outputFile);
            write(ast, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
