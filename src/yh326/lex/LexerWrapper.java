package yh326.lex;

import yh326.gen.lexer;
import yh326.gen.sym;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java_cup.runtime.Symbol;

public class LexerWrapper {
	public static void Lexing(String source_path, String dest_path, String file_path) {
		String absolute_file_path;
		String absolute_output_path;
		File temp = new File(file_path);
		if (temp.isAbsolute()) {
			absolute_file_path = file_path;
		}
		else {
			absolute_file_path = source_path + "/" + file_path;
		}
		absolute_output_path = dest_path + "/" + file_path.substring(file_path.lastIndexOf("/") + 1, file_path.lastIndexOf(".")) + ".lexed";
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
