package yh326;

import java_cup.*;
import yh326.XiLexer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ParserWrapper {
	private static final boolean INTERFACE = false;
	private static final boolean REGULAR = true;

	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	public static void main(String[] argv) {
		ArrayList<String> argv_alist = new ArrayList<String> (Arrays.asList(argv));
		String input_source = "";
		String diag_dump_path = "";
		if (argv_alist.contains("--help")) {
			System.out.println("option --help to show this synopsis.");
			System.out.println("option --lex to show the result from lexical analysis.");
			System.out.println("option --parse to show the result from syntatical analysis.");
			System.out.println("option -sourcepath <path> specifies where to find input source files.");
			System.out.println("option -D <path> specifies where to place generated diagnostic files.");
		}
		if (argv_alist.contains("-sourcepath")) {
			try {
				input_source = argv[argv_alist.indexOf("-sourcepath") + 1];
			}
			catch (IndexOutOfBoundsException e){
				System.out.println("input format incorrect");
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (argv_alist.contains("-D")) {
			try {
				diag_dump_path = argv[argv_alist.indexOf("-D") + 1];
			}
			catch (IndexOutOfBoundsException e){
				System.out.println("input format incorrect");
				e.printStackTrace();
				System.exit(1);
			}
		}
		if (argv_alist.contains("--lex")) {
			/**
			 * TODO
			 * invoke pa1
			 */
			System.exit(0);
		}
		if (argv_alist.contains("--parse")) {
			if (argv.length == 1) {
				System.out.println("Need the path to the file");
				System.exit(1);
			}
			String name = argv[1].split("\\.")[0];
			String postfix = argv[1].split("\\.")[1];
			PostfixType type;
			if (postfix.equals("ixi")) {
				type = PostfixType.INTERFACE;
			}
			else if (postfix.equals("xi")) {
				type = PostfixType.REGULAR;
			}
			else {
				System.out.println("file postfix isn't correct");
				System.exit(1);
			}

			String pwd = System.getProperty("user.dir");
			String sourcePath = pwd + "/" + argv[1];
			String solutionPath = pwd + "/" + name + ".parsed";
			try {
				// TODO: the scanner that we build (jflex) needs to implement sym and java_cup.runtime.scanner
				XiLexer x = new XiLexer(new FileReader(sourcePath));
				parser p = new parser(x);
				Object result = p.parse();
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
				//System.exit(1);
			}

		}
		System.exit(1);
	}
}
