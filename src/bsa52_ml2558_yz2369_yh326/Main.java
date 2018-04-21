package bsa52_ml2558_yz2369_yh326;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyWrapper;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.exception.LexingException;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.exception.TypecheckingException;
import bsa52_ml2558_yz2369_yh326.exception.XiException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.ir.IRWrapper;
import bsa52_ml2558_yz2369_yh326.lex.LexerWrapper;
import bsa52_ml2558_yz2369_yh326.parse.ParserWrapper;
import bsa52_ml2558_yz2369_yh326.tiling.MaxMunch;
import bsa52_ml2558_yz2369_yh326.tiling.tile.Tile;
import bsa52_ml2558_yz2369_yh326.typecheck.TypecheckerWrapper;
import bsa52_ml2558_yz2369_yh326.util.Settings;
import edu.cornell.cs.cs4120.xic.ir.IRNode;

public class Main {

    public static void Usage() {
        String usage = "Usage: xic [options] <source files>\n";
        usage += "  --help           Print this synopsis.\n";
        usage += "  --report-opts    Output a list of optimizations supported by the compiler.\n";
        usage += "  --lex            Generate output from lexical analysis.\n";
        usage += "  --parse          Generate output from syntatical analysis.\n";
        usage += "  --typecheck      Generate output from semantic analysis.\n";
        usage += "  --irgen          Generate output from intermediate code generation.\n";
        usage += "  --irrun          Interpret generated intermediate code (optional).\n";
        usage += "  --optir <phase>  Report the intermediate code at the specified phase of optimization.\n";
        usage += "  --optcfg <phase> Report the control-flow graph at the specified phase of optimization.\n";
        usage += "  -sourcepath <path> Specify where to find input source files.\n";
        usage += "  -libpath <path>  Specify where to find library interface files.\n";
        usage += "  -D <path>        Specify where to place generated diagnostic files.\n";
        usage += "  -d <path>        Specify where to place generated assembly output files.\n";
        usage += "  -target <OS>     Specify the operating system for which to generate code.\n";
        usage += "  -O<opt>          Enable optimization <opt>.\n";
        usage += "  -O               Disable all optimizations.\n";
        usage += "  -O-no-<opt>      Disable only optimization <opt>.\n";

        usage += "\nOptions for internal usage:\n";
        usage += "  --irlow          Print canonical ir code representation.\n";
        usage += "  --abstract       Generate abstract assembly .aasm file.\n";
        usage += "  --disasmgen      Generate acutal assembly by transforming abtract assembly.\n";
        usage += "  --comment        Add comments to generated assembly.\n";
        usage += "  --brentHack      Does whatever Brent wants it to do, for testing.\n";

        System.out.println(usage);
    }

    public static void Support() {
        System.out.println("cf");
    }

    public static void HandleArgv(String[] argv) {

        // Processing other options
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-O")) {
                Settings.optimization = false;
            } else if (argv[i].startsWith("-O-no-")) {
                Settings.noOpts.add(argv[i].substring(6));
            } else if (argv[i].startsWith("-O")) {
                Settings.noOpts.add(argv[i].substring(2));
            } else if (argv[i].startsWith("--")) {
                if (argv[i].equals("--help")) {
                    Usage();
                }
                else if (argv[i].equals("--aasmGraph")) {
                    Settings.brentHack = true;
                }
                else if (argv[i].equals("--report-opts")) {
                    Support();
                } else if (argv[i].equals("--lex")) {
                    Settings.lex = true;
                } else if (argv[i].equals("--parse")) {
                    Settings.parse = true;
                } else if (argv[i].equals("--typecheck")) {
                    Settings.typeCheck = true;
                } else if (argv[i].equals("--irgen")) {
                    Settings.irgen = true;
                } else if (argv[i].equals("--irrun")) {
                    Settings.irrun = true;
                } else if (argv[i].equals("--optir")) {
                    i++;
                    Settings.optIRList.add(argv[i]);
                } else if (argv[i].equals("--optcfg")) {
                    i++;
                    Settings.optCFGList.add(argv[i]);
                } else if (argv[i].equals("--abstract")) {
                    Settings.genAbstract = true;
                } else if (argv[i].equals("--disasmgen")) {
                    Settings.disAsmGen = true;
                } else if (argv[i].equals("--comment")) {
                    Settings.asmComments = true;
                } else {
                    System.out.println("Unrecognized option \"" + argv[i] + "\". Ignoring.");
                }
            } else if (argv[i].startsWith("-")) {
                if (argv[i].equals("-sourcepath")) {
                    Settings.inputSourcePath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-libpath")) {
                    Settings.libPath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-D")) {
                    Settings.outputPath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-d")) {
                    Settings.assemblyOutputPath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-target")) {
                    if (!argv[i + 1].equals("linux"))
                        System.out.println("Unsupported target OS. Using \"linux\"");
                } else {
                    System.out.println("Unrecognized option \"" + argv[i] + "\". Ignoring.");
                    i--; // Cancel the i++ below
                }
                i++;
            } else if (argv[i].endsWith(".ixi")) {
                Settings.ixiList.add(argv[i].substring(0, argv[i].length() - 4));
            } else if (argv[i].endsWith(".xi")) {
                Settings.xiList.add(argv[i].substring(0, argv[i].length() - 3));
            } else {
                System.out.println("Unrecognized file \"" + argv[i] + "\". Ignoring.");
            }
        }
    }

    public static void main(String[] argv) {
        HandleArgv(argv);

        // For each interface file, diagnose.
        for (String file : Settings.ixiList) {
            String inputFile = realPath(Settings.inputSourcePath, file) + ".ixi";
            String outputFile = realPath(Settings.outputPath, file);

            try {
                lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
                ParserWrapper.Parsing(xiLexer, inputFile, outputFile, ".iparsed");
            } catch (LexingException | ParsingException e) {
                e.print(file + ".ixi");
                if (Settings.parse)
                    WriteException(outputFile + ".iparsed", e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Foe each xi file, diagnose.
        for (String file : Settings.xiList) {
            String inputFile = realPath(Settings.inputSourcePath, file) + ".xi";
            String outputFile = realPath(Settings.outputPath, file);
            String assmOutputFile = realPath(Settings.assemblyOutputPath, file);

            // Lexing, parsing, type checking, IR generation
            try {
                lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
                Node ast = ParserWrapper.Parsing(xiLexer, inputFile, outputFile, ".parsed");
                ast.fileName = file + ".xi";
                ast = TypecheckerWrapper.Typechecking(ast, outputFile);
                IRNode irNode = IRWrapper.IRGeneration(ast, outputFile);
                if (Settings.irrun)
                    IRWrapper.IRRun(irNode);
                Tile rootTile = MaxMunch.munch(irNode);
                AssemblyWrapper.GenerateAssembly(rootTile, assmOutputFile);
            } catch (LexingException | ParsingException e) {
                e.print(file + ".xi");
                if (Settings.typeCheck) {
                    WriteException(outputFile + ".typed", e);
                } else if (Settings.parse) {
                    WriteException(outputFile + ".parsed", e);
                }
            } catch (TypecheckingException e) {
                e.print(file + ".xi");
                if (Settings.typeCheck) {
                    WriteException(outputFile + ".typed", e);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
