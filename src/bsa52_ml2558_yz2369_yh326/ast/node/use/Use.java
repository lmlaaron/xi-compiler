package bsa52_ml2558_yz2369_yh326.ast.node.use;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import bsa52_ml2558_yz2369_yh326.Main;
import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.exception.LexingException;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
<<<<<<< HEAD
import bsa52_ml2558_yz2369_yh326.gen.parser;
=======
import bsa52_ml2558_yz2369_yh326.lex.LexerWrapper;
import bsa52_ml2558_yz2369_yh326.parse.ParserWrapper;
>>>>>>> efedb60c5f7617fa387f26535d995d1531225bd7
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class Use extends Node {
    private Identifier id;
    private Node ast;

    public Use(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "use"), id);
        this.id = id;
    }
    
    @Override
    public void loadClasses(SymbolTable sTable, String libPath) throws Exception {
        String inputFile = Paths.get(libPath, id.value).toString() + ".ixi";
        String outputFile = Paths.get(Settings.outputPath, id.value).toString();
        if (!(new File(inputFile).exists()))
            throw new OtherException(line, col, "Interface file " + id.value + ".ixi not found.");
        
        try {
            FileReader fileReader = new FileReader(inputFile);
            lexer xiLexer = LexerWrapper.Lexing(fileReader, outputFile);
            ast = ParserWrapper.Parsing(xiLexer, outputFile, ".iparsed");
            ast.loadClasses(sTable, libPath);
        } catch (LexingException | ParsingException e) {
            e.print(id.value + ".ixi");
            if (Settings.typeCheck) {
                Main.WriteException(outputFile + ".typed", e);
            } else if (Settings.parse) {
                Main.WriteException(outputFile + ".parsed", e);
            }
            throw new OtherException(line, col, "Failed to parse or typecheck interface file.");
        }
    }

    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        ast.loadMethods(sTable);
    }
}
