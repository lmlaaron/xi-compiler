package yh326.ast.node.use;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Keyword;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.exception.ParsingException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class Use extends Node {
    private Identifier id;
    
    public Use(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "use"), id);
        this.id = id;
    }
    
    public void loadMethods(SymbolTable sTable) throws Exception {
        File lib = new File(id + ".ixi");
        if (!lib.exists()) {
            lib = new File(System.getProperty("user.dir") + "/lib/xi/" + id.value + ".ixi");
        }
        if (!lib.exists()) {
            throw new RuntimeException("Interface file " + id.value + ".ixi not found.");
        }
        
        try {
            lexer x = new lexer(new FileReader(lib));
            parser p = new parser(x);
            try {
                Node ast = (Node) p.parse().value;
                ast.loadMethods(sTable);
            } catch (Exception e) {
                throw new ParsingException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
