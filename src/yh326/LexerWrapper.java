package yh326;

import java.io.*;
import java_cup.runtime.Symbol;

public class LexerWrapper {
	public static void Lexing(String sourcePath, String destPath, String filename) {
        String filePath = sourcePath + "/" + filename;
		String outputPath = destPath + "/" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + ".lexed";
		try {
			XiLexer xiLexer = new XiLexer(new FileReader(filePath));
			FileWriter writer = new FileWriter(outputPath);
			Symbol s = xiLexer.next_token();
			while (s.sym != sym.EOF) {
				writer.write(s.toString() + "\n");
				//if (s.sym == sym.ERROR) break;
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
