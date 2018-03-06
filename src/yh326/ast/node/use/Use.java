package yh326.ast.node.use;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Keyword;
import yh326.ast.node.Node;
import yh326.exception.OtherException;
import yh326.exception.ParsingException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class Use extends Node {
    private Identifier id;

    public Use(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "use"), id);
        this.id = id;
    }

    @Override
    public void loadMethods(SymbolTable sTable, String libPath) throws Exception {
        File lib = new File(Paths.get(libPath, id.value + ".ixi").toString());
        if (!lib.exists()) {
            throw new OtherException(line, col, "Interface file " + id.value + ".ixi not found.");
        }

        try {
            lexer x = new lexer(new FileReader(lib));
            @SuppressWarnings("deprecation")
            parser p = new parser(x);
            Node ast;
            try {
                ast = (Node) p.parse().value;
            } catch (Exception e) {
                throw (ParsingException) e;
            }
            ast.loadMethods(sTable);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
