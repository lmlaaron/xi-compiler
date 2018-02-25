package yh326.lex;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java_cup.runtime.Symbol;

import yh326.gen.lexer;
import yh326.gen.sym;

public class LexerWrapper {
	/**
	 * 
	 * @param absolute_file_path, an absolute path to the input file
	 * @param dest_path, an absolute path to the output directory
	 */
	public static void Lexing(String absolute_file_path, String dest_path) {
		// generate the complete output path
		String absolute_output_path = 
				dest_path + 
				"/" + 
				absolute_file_path.substring(absolute_file_path.lastIndexOf("/") + 1, absolute_file_path.lastIndexOf(".")) + "lexed";
		
		try {
			lexer xiLexer = new lexer(new FileReader(absolute_file_path));
			FileWriter writer = new FileWriter(absolute_output_path);
			Symbol s = xiLexer.next_token();
			while (s.sym != sym.EOF) {
				writer.write(s.toString() + "\n");
				if (s.sym == sym.ERROR) break;
				s = xiLexer.next_token();
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
