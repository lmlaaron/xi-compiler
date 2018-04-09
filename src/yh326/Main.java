package yh326;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import edu.cornell.cs.cs4120.xic.ir.IRNode;
import yh326.assembly.Assembly;
import yh326.ast.node.Node;
import yh326.exception.LexingException;
import yh326.exception.ParsingException;
import yh326.exception.TypecheckingException;
import yh326.exception.XiException;
import yh326.gen.lexer;
import yh326.ir.IRWrapper;
import yh326.lex.LexerWrapper;
import yh326.parse.ParserWrapper;
import yh326.tiling.MaxMunch;
import yh326.tiling.tile.Tile;
import yh326.typecheck.TypecheckerWrapper;
import yh326.util.Flags;

public class Main {

    public static void main(String[] argv) {
        // Note: all ".replace('\\', '/')" is to support Windows 10 path convention.
        ArrayList<String> argvArray = new ArrayList<String>(Arrays.asList(argv));

        // Source, output, library file location (relative pwd by default)
        String inputSourcePath = ".";
        String outputPath = ".";
        String libPath = ".";
        String assemblyOutputPath = ".";
        String targetOS = "linux";
        boolean optimization = true;

        // Detect options
        if (argvArray.contains("--help")) {
            System.out.println("option --help to show this synopsis.");
            System.out.println("option --lex to show the result from lexical analysis.");
            System.out.println("option --parse to show the result from syntatical analysis.");
            System.out.println("option --typecheck to show the result from type checking.");
            System.out.println("option --irgen to show lowered ir code representation.");
            System.out.println("option --irlow (intended for internal use) to show canonical ir code representation.");
            System.out.println(
                    "option --asmgen (interal usage) generate acutal assembly by transforming abtract assembly");
            System.out.println("option --irrun to simulate running translated IR code.");
            System.out.println("option -sourcepath <path> to specify where to find input source files.");
            System.out.println("option -libpath <path> to specify where to find library interface files.");
            System.out.println("option -D <path> to specify where to place generated diagnostic files.");
            System.out.println("option -d <path> to specify where to place generated assembly output files.");
            System.out.println("option -O to diable optimizations.");
            System.out.println("option -target <OS> to specify the operating system for which to generate code.");
            System.out.println("option --comment to add comments to generated assembly");
        }

        if (argvArray.contains("--comment"))
            Flags.asmComments = true;

        // Processing other options
        try {
            if (argvArray.contains("-sourcepath")) {
                inputSourcePath = argv[argvArray.indexOf("-sourcepath") + 1].replace('\\', '/');
            }
            if (argvArray.contains("-D")) {
                outputPath = argv[argvArray.indexOf("-D") + 1].replace('\\', '/');
            }
            if (argvArray.contains("-d")) {
                assemblyOutputPath = argv[argvArray.indexOf("-d") + 1].replace('\\', '/');
            }
            if (argvArray.contains("-libpath")) {
                libPath = argv[argvArray.indexOf("-libpath") + 1].replace('\\', '/');
            }
            if (argvArray.contains("-O")) {
                optimization = false;
            }
            if (argvArray.contains("-target")) {
                targetOS = argv[argvArray.indexOf("-target") + 1];
                if (!targetOS.equals("linux")) {
                    System.out.println("Unsupported target OS.");
                    System.exit(1);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("input format incorrect");
            e.printStackTrace();
            System.exit(1);
        }

        // Convert to absolute path
        inputSourcePath = Paths.get(inputSourcePath).toAbsolutePath().toString();
        outputPath = Paths.get(outputPath).toAbsolutePath().toString();
        libPath = Paths.get(libPath).toAbsolutePath().toString();

        // Add all source files (.xi or .ixi file)
        ArrayList<String> sourceFiles = new ArrayList<String>();
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].indexOf(".xi") != -1 || argv[i].indexOf(".ixi") != -1) {
                sourceFiles.add(argv[i].replace('\\', '/')); // to support operations in Windows 10
            }
        }

        // For each file, diagnose.
        for (String sourceFile : sourceFiles) {
            // Get real source path and output path. For example, if
            // inputSourcePath is "./a/b" and sourceFile is "c/d.xi", then
            // the real source path is "./a/b/c". Same for real output path.
            String realInputFile = realPath(inputSourcePath, sourceFile);
            String realOutputDir = realPath(outputPath, sourceFile);
            realOutputDir = realOutputDir.substring(0, realOutputDir.lastIndexOf("/"));
            String extension = sourceFile.substring(sourceFile.lastIndexOf(".") + 1);
            String fileName = sourceFile.substring(sourceFile.lastIndexOf("/") + 1, sourceFile.lastIndexOf("."));
            String realOutputFile = Paths.get(realOutputDir, fileName).toString();

            try {
                // ====== LEXING ======
                lexer xiLexer = LexerWrapper.Lexing(realInputFile);
                if (argvArray.contains("--lex")) {
                    LexerWrapper.WriteLexingResult(xiLexer, realOutputFile + ".lexed");
                }

                // ====== PARSING ======
                Node ast = ParserWrapper.Parsing(xiLexer, realInputFile, extension);
                ast.fileName = fileName;
                String outExtension = extension.equals("xi") ? ".parsed" : ".iparsed";
                if (argvArray.contains("--parse")) {
                    ParserWrapper.WriteParsingResult(ast, realOutputFile + outExtension);
                }
                if (!extension.equals("xi"))
                    continue;

                // ====== TYPE-CHECKING ======
                ast = TypecheckerWrapper.Typechecking(ast, realInputFile, libPath);
                if (argvArray.contains("--typecheck")) {
                    TypecheckerWrapper.WriteTypecheckingResult(realOutputFile + ".typed");
                }

                // ====== IR GENERATION ======
                IRNode irNode = IRWrapper.IRGeneration(ast, realInputFile, optimization);
                if (argvArray.contains("--irgen")) {
                    IRWrapper.WriteIRResult(irNode, realOutputFile + ".ir");
                }
                if (argvArray.contains("--irrun")) {
                    IRWrapper.IRRun(irNode);
                }

                String realAssemblyOutputDir = realPath(assemblyOutputPath, sourceFile);
                realAssemblyOutputDir = realAssemblyOutputDir.substring(0, realAssemblyOutputDir.lastIndexOf("/"));

                // ======= ASSEMBLY GENERATION ======= 
                Tile rootTile = MaxMunch.munch(irNode);
                Assembly assm = rootTile.generateAssembly();
                // ======= ACTUAL ASSEMBLY GENERATION BY SPILLING REGISTER ===

                if (!argvArray.contains("--disasmgen")) {
                    assm = assm.registerAlloc();
                }
                // ======= END ACTUAL ASSEMBLY GENERATION ==========
                if (assm.incomplete()) {
                    System.out.println("Incomplete assembly code!:");
                    System.out.println(assm.toString());
                } else {
                    // write assembly to file
                    File assmF = null;
                    if (argvArray.contains("--disasmgen")) {
                        assmF = new File(realOutputFile + ".ra.s");
                    } else {
                        assmF = new File(realOutputFile + ".s");
                    }
                    BufferedWriter writer = new BufferedWriter(new FileWriter(assmF));
                    writer.write(".intel_syntax noprefix " + "\n");
                    // intel syntax annotation
                    writer.write(assm.toString());
                    writer.close();
                }

            } catch (LexingException | ParsingException e) {
                e.print(fileName);
                if (argvArray.contains("--parse")) {
                    String outExtension = extension.equals("xi") ? ".parsed" : ".iparsed";
                    WriteException(realOutputFile + outExtension, e);
                } else if (argvArray.contains("--typecheck")) {
                    WriteException(realOutputFile + ".typed", e);
                }
            } catch (TypecheckingException e) {
                e.print(fileName);
                if (argvArray.contains("--typecheck")) {
                    WriteException(realOutputFile + ".typed", e);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return;
    }

    /**
     * Combine the "path" and sourceFile from the argument into a single string,
     * that directly points to the location of the file, and would ignore the "path"
     * if the sourceFile is an absolute path already.
     * 
     * @param path
     *            Absolute path of input source or destination
     * @param sourceFile
     *            Relative or absolute .xi or .ixi file path
     * @return an absolute path that points to the file
     */
    public static String realPath(String path, String sourceFile) {
        if (new File(sourceFile).isAbsolute()) {
            return sourceFile;
        } else {
            return Paths.get(path, sourceFile).toString();
        }
    }

    public static void WriteException(String realOutputFile, XiException e) {
        try {
            FileWriter writer = new FileWriter(realOutputFile);
            writer.write(e.getMessage() + "\n");
            writer.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
