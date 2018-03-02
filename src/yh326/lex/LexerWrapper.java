package yh326.lex;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;

import java_cup.runtime.Symbol;

import yh326.gen.lexer;
import yh326.gen.sym;

public class LexerWrapper {
	/**
	 * 
	 * @param realInputFile, an absolute path to the input file
	 * @param realOutputDir, an absolute path to the output directory
	 */
	public static void Lexing(String realInputFile, String realOutputDir, String fileName) {
		// generate the complete output path
	    String outputFileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".lexed";
		String realOutputFile = Paths.get(realOutputDir, outputFileName).toString();	
		try {
			lexer xiLexer = new lexer(new FileReader(realInputFile));
			FileWriter writer = new FileWriter(realOutputFile);
			Symbol s = xiLexer.next_token();
			while (s.sym != sym.EOF) {
				writer.write(s.toString() + "\n");
				if (s.sym == sym.ERROR) break;
				s = xiLexer.next_token();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
}
