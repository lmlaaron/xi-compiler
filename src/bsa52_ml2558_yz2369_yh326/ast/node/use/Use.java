package bsa52_ml2558_yz2369_yh326.ast.node.use;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

import bsa52_ml2558_yz2369_yh326.Main;
import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.exception.LexingException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.exception.TypecheckingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.lex.LexerWrapper;
import bsa52_ml2558_yz2369_yh326.parse.ParserWrapper;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class Use extends Node {
    private Identifier id;
    private Node ast;
    private boolean alreadyImported;

    public Use(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "use"), id);
        this.id = id;
        this.alreadyImported = false;
    }
    
    @Override
    public void loadClasses(SymbolTable sTable, Set<String> libPaths) throws Exception {
        if (sTable.setInterfaceImported(id.value) == false) {
            alreadyImported = true;
            return;
        }
        String inputFile = null;
        for (String libPath : libPaths) {
            inputFile = Paths.get(libPath, id.value).toString() + ".ixi";
            if (new File(inputFile).exists()) 
                break;
        }
        String outputFile = Main.realPath(Settings.outputPath, id.value);
        if (!(new File(inputFile).exists()))
            throw new OtherException(line, col, "Interface file " + id.value + ".ixi not found.");
        
        try {
            lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
            ast = ParserWrapper.Parsing(xiLexer, outputFile, ".iparsed");
            ast.loadClasses(sTable, libPaths);
        } catch (LexingException | ParsingException e) {
            e.print(id.value + ".ixi");
            if (Settings.typeCheck) {
                Main.WriteException(outputFile + ".typed", e);
            } else if (Settings.parse) {
                Main.WriteException(outputFile + ".parsed", e);
            }
            throw new OtherException(line, col, "Failed to parse or typecheck interface file1.");
        }
    }

    @Override
    public void loadMethods(SymbolTable sTable, Set<String> libPaths) throws Exception {
        if (!alreadyImported) {
            try {
                /*String inputFile = null;
                for (String libPath : libPaths) {
                    inputFile = Paths.get(libPath, id.value).toString() + ".ixi";
                    if (new File(inputFile).exists()) 
                        break;
                }
                String outputFile = Main.realPath(Settings.outputPath, id.value);
                if (!(new File(inputFile).exists()))
                    throw new OtherException(line, col, "Interface file " + id.value + ".ixi not found.");
                lexer xiLexer = LexerWrapper.Lexing(inputFile, outputFile);
                ast = ParserWrapper.Parsing(xiLexer, outputFile, ".iparsed");*/
                ast.loadMethods(sTable, libPaths);
            } catch (TypecheckingException e) {
                e.print(id.value + ".xi");
                if (Settings.typeCheck) {
                    String outputFile = Main.realPath(Settings.outputPath, id.value);
                    Main.WriteException(outputFile + ".typed", e);
                }
                throw new OtherException(line, col, "Failed to parse or typecheck interface file3.");
            }
        }
    }
}
