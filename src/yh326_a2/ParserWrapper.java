package yh326_a2;

import java_cup.*;
import java.io.*;

public class ParserWrapper {
	private static final boolean INTERFACE = false;
	private static final boolean REGULAR = true;
	
	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	public static void main(String[] argv) {
		if (argv[0].equals("--help")) {
			/**
			 * TODO
			 * print synopsis
			 */
			System.exit(0);
		}
		else if (argv[0].equals("--lex")) {
			/**
			 * TODO
			 * invoke pa1
			 */
			System.exit(0);
		}
		else if (argv[0].equals("--parse")) {
			if (argv.length == 1) {
				System.out.println("Need the path to the file");
				System.exit(-1);
			}
			String name = argv[1].split("\\.")[0];
			String postfix = argv[1].split("\\.")[1];
			PostfixType type;
			if (postfix.equals("--ixi")) {
				type = PostfixType.INTERFACE;
			}
			else if (postfix.equals(".xi")) {
				type = PostfixType.REGULAR;
			}
			else {
				System.out.println("file postfix isn't correct");
				System.exit(-1);
			}
			
			String pwd = System.getProperty("user.dir");
			String sourcePath = pwd + "/" + argv[1];
			String solutionPath = pwd + "/" + name + ".parsed";
			try {
				// TODO: the scanner that we build (jflex) needs to implement sym and java_cup.runtime.scanner
				Scanner s = new Scanner(new FileReader(sourcePath));
				parser p = new parser(s, sf);
				
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
				//System.exit(1);
			}
			
		}
		else if (argv[0].equals("-sourcepath")) {
			/**
			 * TODO
			 * see handout
			 */
		}
		else if (argv[0].equals("-D")) {
			/**
			 * TODO
			 * see handout
			 */
		}
		else {
			System.out.println("incorrect option argument");
			System.exit(-1);
		}
	}
}