package yh326_a1;

import java.io.*;
import java_cup.runtime.Symbol;

public class LexerWrapper {
	public static void main(String[] argv) {
                if (argv.length == 0) {
			System.out.println("xi file not given.");
                        return;
		}
		String pwd = System.getProperty("user.dir");
		String sourcePath = pwd + "/" + argv[0];
		String solutionPath = pwd + "/" + argv[0].split("\\.")[0] + ".lexed";
		try {
			XiLexer xiLexer = new XiLexer(/*new UnicodeEscapes(*/new FileReader(sourcePath));
			FileWriter writer = new FileWriter(solutionPath);
			Symbol s = xiLexer.next_token();
			while (s.sym != sym.EOF) {
//				System.out.println(s.toString());
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
