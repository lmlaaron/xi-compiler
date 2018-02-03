package yh326_a1;

import java.io.FileReader;
import java.io.FileWriter;

import yh326_a1.XiLexer.Token;
import yh326_a1.XiLexer.TokenType;

public class LexerWrapper {
	public static void main(String[] argv) {
		if (argv[0].equals("--help") || argv[0].equals("-h")) {
			System.out.println("TODO");
			return;
		}
		if (argv[0].equals("--lex")) {
			String pwd = System.getProperty("user.dir");
			String sourcePath = pwd + "/" + argv[1];
			String solutionPath = pwd + "/" + argv[1].split("\\.")[0] + ".lexed";
			try {
				XiLexer xiLexer = new XiLexer(new FileReader(sourcePath));
				FileWriter writer = new FileWriter(solutionPath);
				Token t = xiLexer.nextToken();
				while (t != null) {
					System.out.println(tokenToString(t));
					writer.write(tokenToString(t) + "\n");
					t = xiLexer.nextToken();
				}
				writer.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
			return;
		}
	}
	
	public static String tokenToString(Token t) {
		if (t.type == TokenType.OPERATOR || t.type == TokenType.SEPARATOR) {
			return t.line + ":" + t.column + " " + t.attribute;
		}
		else if (t.type == TokenType.STRING) {
			int length = t.attribute.toString().length();
			return t.line + ":" + t.column + " " + t.type.toString().toLowerCase() + " " + t.attribute.toString().substring(1, length - 1);
		}
		else {
			return t.line + ":" + t.column + " " + t.type.toString().toLowerCase() + (t.attribute == null ? "" : " " + t.attribute);
		}
	}
}
