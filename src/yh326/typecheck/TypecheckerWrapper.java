package yh326.typecheck;

import java.io.FileReader;
import java.io.FileWriter;

import yh326.ast.SymbolTable;
import yh326.ast.node.Node;
import yh326.exception.ParsingException;
import yh326.exception.SemanticCheckException;
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
	public static void Typechecking(String absolute_file_path, String dest_path, String libPath) {
		
		// generate the output postfix
        String postfix = absolute_file_path.substring(absolute_file_path.lastIndexOf(".") + 1);
        String out_postfix=".typed";
		if (postfix.equals("ixi")) out_postfix=".typed";
		else if (postfix.equals("xi")) out_postfix=".typed";
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
			@SuppressWarnings("deprecation")
            parser p = new parser(x);
			try {
			    Node ast = (Node) p.parse().value;
			    SymbolTable sTable = new SymbolTable();
			    ast.loadMethods(sTable, libPath);
			    ast.typeCheck(sTable);
	            writer.write("Valid Xi Program");
            } catch (ParsingException e) {
                writer.write(e.getMessage() + "\n");
            } catch (SemanticCheckException e) {
                //e.printStackTrace(); // TODO delete this
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
