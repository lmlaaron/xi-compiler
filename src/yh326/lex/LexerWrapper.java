package yh326.lex;

import yh326.gen.lexer;
import yh326.gen.sym;

import java.io.FileReader;
import java.io.FileWriter;
import java_cup.runtime.Symbol;

public class LexerWrapper {
	public static void Lexing(String sourcePath, String destPath, String filename) {
        String filePath = sourcePath + "/" + filename;
		String outputPath = destPath + "/" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + ".lexed";
		try {
			lexer xiLexer = new lexer(new FileReader(filePath));
			FileWriter writer = new FileWriter(outputPath);
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
