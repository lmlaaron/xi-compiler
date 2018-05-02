package bsa52_ml2558_yz2369_yh326.ast.node.use;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

import bsa52_ml2558_yz2369_yh326.ast.SymbolTable;
import bsa52_ml2558_yz2369_yh326.ast.node.Node;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Identifier;
import bsa52_ml2558_yz2369_yh326.ast.node.misc.Keyword;
import bsa52_ml2558_yz2369_yh326.exception.OtherException;
import bsa52_ml2558_yz2369_yh326.exception.ParsingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.gen.parser;

public class Use extends Node {
    private Identifier id;

    public Use(int line, int col, Identifier id) {
        super(line, col, new Keyword(line, col, "use"), id);
        this.id = id;
    }
    
    @Override
    public void loadClasses(SymbolTable sTable, String libPath) throws Exception {
        System.out.println("TO BE IMPLEMENTED.");
        
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
