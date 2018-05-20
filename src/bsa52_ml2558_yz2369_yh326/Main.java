package bsa52_ml2558_yz2369_yh326;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import bsa52_ml2558_yz2369_yh326.assembly.AssemblyWrapper;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClass;
import bsa52_ml2558_yz2369_yh326.ast.node.classdecl.XiClasses;
import bsa52_ml2558_yz2369_yh326.ast.procedures.InitializeToZero;
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
        usage += "  --debugast       Print a tree consisting of the type of each node in the parse tree\n";
        usage += "  --defaultvalues  Alters AST to set unassigned variables to their default values\n";
        //usage += "  --brentHack      Does whatever Brent wants it to do, for testing.\n";

        System.out.println(usage);
    }

    public static void HandleArgv(String[] argv) {
        if (argv.length == 0) {
            Usage();
            return;
        }
        
        boolean optimization = true;
        List<String> opts = new ArrayList<String>();
        List<String> noOpts = new ArrayList<String>();
        
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals("-O")) {
                optimization = false;
            } else if (argv[i].startsWith("-O-no-")) {
                String opt = argv[i].substring(6);
                if (!Settings.supportedOpt.contains(opt))
                    System.out.println("WARNING: unrecognized optimization \"" + opt + "\". Ignoring.");
                noOpts.add(opt);
            } else if (argv[i].startsWith("-O")) {
                String opt = argv[i].substring(2);
                if (!Settings.supportedOpt.contains(opt))
                    System.out.println("WARNING: unrecognized optimization \"" + opt + "\". Ignoring.");
                opts.add(opt);
            } else if (argv[i].startsWith("--")) {
                if (argv[i].equals("--help")) {
                    Usage();
                } else if (argv[i].equals("--report-opts")) {
                    Settings.supportedOpt.forEach(opt -> System.out.println(opt));
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
                    Settings.optIRSet.add(argv[i]);
                } else if (argv[i].equals("--optcfg")) {
                    i++;
                    Settings.optCFGSet.add(argv[i]);
                } else if (argv[i].equals("--abstract")) {
                    Settings.genAbstract = true;
                } else if (argv[i].equals("--disasmgen")) {
                    Settings.disAsmGen = true;
                } else if (argv[i].equals("--comment")) {
                    Settings.asmComments = true;
                } else if (argv[i].equals("--debugast")) {
                    Settings.debugAst = true;
                } else if (argv[i].equals("--defaultvalues")) {
                    Settings.defaultValues = true;
                }
                else {
                    System.out.println("WARNING: unrecognized option \"" + argv[i] + "\". Ignoring.");
                }
            } else if (argv[i].startsWith("-")) {
                if (argv[i].equals("-sourcepath")) {
                    Settings.inputSourcePath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-libpath")) {
                    Settings.libPath.add(Paths.get(argv[i + 1]).toAbsolutePath().toString());
                } else if (argv[i].equals("-D")) {
                    Settings.outputPath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-d")) {
                    Settings.assemblyOutputPath = Paths.get(argv[i + 1]).toAbsolutePath().toString();
                } else if (argv[i].equals("-target")) {
                    if (!argv[i + 1].equals("linux"))
                        System.out.println("WARNING: unsupported target OS. Using \"linux\"");
                } else {
                    System.out.println("WARNING: unrecognized option \"" + argv[i] + "\". Ignoring.");
                    i--; // Cancel the i++ below
                }
                i++;
            } else if (argv[i].endsWith(".ixi")) {
                Settings.ixiList.add(argv[i].substring(0, argv[i].length() - 4));
            } else if (argv[i].endsWith(".xi")) {
                Settings.xiList.add(argv[i].substring(0, argv[i].length() - 3));
            } else {
                System.out.println("WARNING: unrecognized argument \"" + argv[i] + "\". Ignoring.");
            }
        }
        
        // Detect conflicting optimization options
        if ((!optimization && (!noOpts.isEmpty() || !opts.isEmpty())) || 
                (!noOpts.isEmpty() && !opts.isEmpty())) {
            System.out.println("WARNING: conflicting optimization option detected. Set to do all optimizations.");
        } else if (!optimization) {
            Settings.opts.clear();
        } else if (!opts.isEmpty()) {
            Settings.opts.retainAll(opts);
        } else if (!noOpts.isEmpty()) {
            Settings.opts.removeAll(noOpts);
        }

        // hardcode register allocation to be on, because pa7 breaks backwards compatibility
        Settings.opts.add("reg");
        // these optimizations no longer work
        Settings.opts.remove("copy");
        Settings.opts.remove("dce");
    }

    public static void main(String[] argv) {
        HandleArgv(argv);

        // For each interface file, diagnose.
        for (String file : Settings.ixiList) {
            String inputFile = realPath(Settings.inputSourcePath, file) + ".ixi";
            Settings.libPath.add(inputFile.substring(0, inputFile.lastIndexOf("/")));
            String outputFile = realPath(Settings.outputPath, file);

            try {
                lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
                ParserWrapper.Parsing(xiLexer, outputFile, ".iparsed");
            } catch (LexingException | ParsingException e) {
                e.print(file + ".ixi");
                if (Settings.parse)
                    WriteException(outputFile + ".iparsed", e);
            } catch (FileNotFoundException e) {
                System.out.println("WARNING: \"" + inputFile + "\" not found. Ignoring.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // For each xi file, diagnose.
        for (String file : Settings.xiList) {
            String inputFile = realPath(Settings.inputSourcePath, file) + ".xi";
            Settings.libPath.add(inputFile.substring(0, inputFile.lastIndexOf("/")));
            String outputFile = realPath(Settings.outputPath, file);
            String assmOutputFile = realPath(Settings.assemblyOutputPath, file);

            // Lexing, parsing, type checking, IR generation
            try {
                lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
                Node ast = ParserWrapper.Parsing(xiLexer, outputFile, ".parsed");
                try {
                    ast = InitializeToZero.do_it(ast);
                }
                catch (Exception e) {
                    // if we requested to see the ast, it should still be displayed:
                    if (Settings.debugAst)
                        ParserWrapper.DebugPrintASTNodeTypes(ast);
                    throw e;
                }
                if (Settings.debugAst) ParserWrapper.DebugPrintASTNodeTypes(ast);
                ast.fileName = file + ".xi";
                ast = TypecheckerWrapper.Typechecking(ast, outputFile);
                XiClasses.postprocessIndices(XiClass.all);
                IRNode irNode = IRWrapper.IRGeneration(ast, outputFile);
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
            } catch (FileNotFoundException e) {
                System.out.println("WARNING: \"" + inputFile + "\" not found. Ignoring.");
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
