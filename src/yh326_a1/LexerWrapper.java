package yh326_a1;

import java.io.FileReader;

public class LexerWrapper {
	public static void main(String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			try{
				System.out.println("lexing: " + argv[i]);
				XiLexer xiLexer = new XiLexer(new FileReader(argv[i]));
				while (true) {
					System.out.println(xiLexer.nextToken().toString());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
