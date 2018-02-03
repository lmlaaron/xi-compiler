package yh326_a1;

import java.io.FileReader;

import yh326_a1.XiLexer.Token;
import yh326_a1.XiLexer.TokenType;

public class LexerWrapper {
	public static void main(String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			try{
				System.out.println("lexing: " + argv[i]);
				XiLexer xiLexer = new XiLexer(new FileReader(argv[i]));
				Token t = xiLexer.nextToken();
				while (t != null) {
					System.out.println(tokenToString(t));
					t = xiLexer.nextToken();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public static String tokenToString(Token t) {
		if (t.type == TokenType.OPERATOR || t.type == TokenType.SEPARATOR) {
			return t.line + ":" + t.column + " " + t.attribute;
		}
		else {
			return t.line + ":" + t.column + " " + t.type.toString().toLowerCase() + " " + (t.attribute == null ? "" : "" + t.attribute);
		}
	}
}
