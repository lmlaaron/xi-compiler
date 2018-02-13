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
				input_source = input_source.replace('\\', '/');
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
				diag_dump_path = diag_dump_path.replace('\\', '/');
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
			//if (argv.length == 1) {
			//	System.out.println("Need the path to the file");
			//	System.exit(1);
			//}
			//String name = argv[1].split("\\.")[0];
			String[] temp = removePostfix(input_source);
			String input_source_without_postfix = temp[0];
			String postfix = temp[1];
			
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
			String absolute_source_path = pwd + "/" + input_source;
			String absolute_solution_path = pwd + "/" + input_source_without_postfix + ".parsed";
			absolute_solution_path = absolute_solution_path.replace('/', '\\');
			try {
				// TODO: the scanner that we build (jflex) needs to implement sym and java_cup.runtime.scanner
				XiLexer x = new XiLexer(new FileReader(absolute_source_path));
				parser p = new parser(x);
				Object result = p.parse().value;
			}
			catch (Exception e) {
				e.printStackTrace();
				return;
				//System.exit(1);
			}
			
		}
		System.exit(1);
	}
	
	public static String[] removePostfix(String source) {
		String[] source_array = source.split("/");
		String filename = source_array[source_array.length - 1];
		String filename_front = filename.split("\\.")[0];
		String postfix = filename.split("\\.")[1];
		source_array[source_array.length - 1] = filename_front;
		String without_postfix = "";
		for (int i = 0; i < source_array.length; i++) {
			without_postfix += source_array[i];
		}
		String[] result = {without_postfix, postfix};
		return result;
	}
}
