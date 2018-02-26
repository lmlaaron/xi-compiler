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

	/**
	 * 
	 * @param absolute_file_path, an absolute path to the input file
	 * @param dest_path, an absolute path to the output directory
	 */
	public static void Typechecking(String absolute_file_path, String dest_path) {
		
		// generate the output postfix
        String postfix = absolute_file_path.substring(absolute_file_path.lastIndexOf(".") + 1);
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
		
		// generate the complete output path
		String absolute_output_path = 
				dest_path + 
				"/" + 
				absolute_file_path.substring(absolute_file_path.lastIndexOf("/") + 1, absolute_file_path.lastIndexOf(".")) + out_postfix;
		
		// typechecking
		try {
		    FileWriter writer = new FileWriter(absolute_output_path);
            lexer x = new lexer(new FileReader(absolute_file_path));
			parser p = new parser(x);
			try {
			    Node ast = (Node) p.parse().value;
			    SymbolTable sTable = new SymbolTable();
			    ast.loadMethods(sTable);
			    ast.typeCheck(sTable);
			    System.out.println("Valid Xi Program");
	            writer.write("Valid Xi Program");
            } catch (ParsingException e) {
                e.printStackTrace();
                writer.write(e.getMessage() + "\n");
            } catch (Exception e) { // TODO TypeChecking exception
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
