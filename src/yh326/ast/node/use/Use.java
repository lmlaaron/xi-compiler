package yh326.ast.node.use;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import yh326.ast.SymbolTable;
import yh326.ast.node.Identifier;
import yh326.ast.node.Keyword;
import yh326.ast.node.Node;
import yh326.ast.type.NodeType;
import yh326.gen.lexer;
import yh326.gen.parser;

public class Use extends Node {
    String id;
    public Use(String id) {
        super(new Keyword("use"), new Identifier(id));
        this.id = id;
    }
    
    public NodeType typeCheck(SymbolTable sTable) throws Exception {
        File lib = new File(id + ".ixi");
        if (!lib.exists()) {
            lib = new File(System.getProperty("user.id") + "/lib/xi/" + id + ".ixi");
        }
        if (!lib.exists()) {
            throw new RuntimeException("Interface file " + id + ".ixi not found.");
        }
        
        lexer x;
        try {
            x = new lexer(new FileReader(lib));
            parser p = new parser(x);
            try {
                Node ast = (Node) p.parse().value;
                ast.typeCheck(sTable);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
        
    }
}
