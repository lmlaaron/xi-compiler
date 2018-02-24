package yh326.typecheck;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.gen.lexer;
import yh326.gen.parser;

public class TypecheckerWrapper {
	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	public static void Typechecking(String source_path, String dest_path, String file_path) {
		
		// generate the output postfix
        String postfix = file_path.substring(file_path.lastIndexOf(".") + 1);
		//PostfixType type;
        String out_postfix=".typed";
		if (postfix.equals("ixi")) {
			//type = PostfixType.INTERFACE;
            out_postfix=".typed";
		}
		else if (postfix.equals("xi")) {
			//type = PostfixType.REGULAR;
            out_postfix=".typed";
		}
		else {
			System.out.println("file postfix isn't correct");
			System.exit(1);
		}
		
		// generate the complete input and output path
		String absolute_file_path;
		String absolute_output_path;
		File temp = new File(file_path);
		if (temp.isAbsolute()) {
			absolute_file_path = file_path;
		}
		else {
			absolute_file_path = source_path + "/" + file_path;
		}
		absolute_output_path = dest_path + "/" + file_path.substring(file_path.lastIndexOf("/") + 1, file_path.lastIndexOf(".")) + out_postfix;
		
		// typecheckomg
		try {
		    FileWriter writer = new FileWriter(absolute_output_path);
            lexer x = new lexer(new FileReader(absolute_file_path));
			parser p = new parser(x);
			try {
			    Node ast = (Node) p.parse().value;
			    SymbolTable sTable = new SymbolTable();
	            ast.typeCheck(sTable);
	            sTable.dumpTable();
            } catch (ParsingException e) {
                System.out.println("Parsing error");
                e.printStackTrace();
                writer.write(e.getMessage() + "\n");
            } catch (Exception e) { // TODO TypeChecking exception
                System.out.println("Typechecking error");
                e.printStackTrace();
                writer.write(e.getMessage() + "\n");
            }
            writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		return;
	}
}
