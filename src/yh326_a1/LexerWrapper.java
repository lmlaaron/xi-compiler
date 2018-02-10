package yh326_a1;

import java.io.*;
import java_cup.runtime.Symbol;

public class LexerWrapper {
	public static void main(String[] argv) {
                if (argv.length == 0) {
//			System.out.println("TODO");
                        return;
		}
		String pwd = System.getProperty("user.dir");
		String sourcePath = pwd + "/" + argv[1];
		String solutionPath = pwd + "/" + argv[1].split("\\.")[0] + ".lexed";
		try {
			XiLexer xiLexer = new XiLexer(/*new UnicodeEscapes(*/new FileReader(sourcePath));
			FileWriter writer = new FileWriter(solutionPath);
			Symbol s = xiLexer.next_token();
			while (s != null) {
//				System.out.println(tokenToString(s));
				writer.write(tokenToString(s) + "\n");
				if (s.sym == sym.ERROR) break;
				s = xiLexer.next_token();
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
			//System.exit(1);
		}
		return;
	}
	public static String tokenToString(Symbol s) {
		if (s.sym == sym.ERROR) {
			return s.left + ":" + s.right + " error" + s.sym;
		}
		else if (s.sym == sym.OPERATOR || s.sym == sym.SEPARATOR || s.sym == sym.DUMMY) {
			return s.left + ":" + s.right + " " + s.sym;
		}
		else if (s.sym == sym.STRING) {
			int length = s.value.toString().length();
			return s.left + ":" + s.right + " string " + s.value.toString().substring(1, length - 1);
		}
		else {
			return s.left + ":" + s.right + " " + s.value == null ? "" : " " + s.value;
		}
	}
}
