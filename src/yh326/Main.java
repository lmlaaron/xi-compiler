package yh326;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import yh326.lex.LexerWrapper;
import yh326.parse.ParserWrapper;
import yh326.typecheck.*;

public class Main {
	//private static final boolean INTERFACE = false;
	//private static final boolean REGULAR = true;

	public static void main(String[] argv) {
		ArrayList<String> argv_alist = new ArrayList<String> (Arrays.asList(argv));
		String input_source = System.getProperty("user.dir");
		input_source = input_source.replace('\\', '/'); // to support operations in Windows 10
		String diag_dump_path = System.getProperty("user.dir");
		diag_dump_path = diag_dump_path.replace('\\', '/'); // to support operations in Windows 10
		String lib_path = System.getProperty("user.dir");
		lib_path = lib_path.replace('\\', '/'); // to support operations in Windows 10
		ArrayList<String> source_files = new ArrayList<String> ();
		for (int i = 0; i < argv.length; i++) {
			if (argv[i].indexOf(".xi") != -1 || argv[i].indexOf(".ixi") != -1) {
				source_files.add(argv[i].replace('\\', '/')); // to support operations in Windows 10
			}
		}
		if (argv_alist.contains("--help")) {
			System.out.println("option --help to show this synopsis.");
			System.out.println("option --lex to show the result from lexical analysis.");
			System.out.println("option --parse to show the result from syntatical analysis.");
			System.out.println("option --typecheck to show the result from type checking.");
			System.out.println("option -sourcepath <path> specifies where to find input source files.");
			System.out.println("option -libpath <path> specify where to find library interface files.");
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
		if (argv_alist.contains("-libpath")) {
        	try {
        		String tail = argv[argv_alist.indexOf("-libpath") + 1];
        		lib_path = Paths.get(tail).toAbsolutePath().toString();
        		lib_path = lib_path.replace('\\', '/');
        	}
        	catch (IndexOutOfBoundsException e){
        		System.out.println("input format incorrect");
        		e.printStackTrace();
        		System.exit(1);
        	}
        }
		if (argv_alist.contains("--lex")) {
			for (String source_file : source_files) LexerWrapper.Lexing(combine(input_source, source_file), diag_dump_path);
		}
		if (argv_alist.contains("--parse")) {
			for (String source_file : source_files) ParserWrapper.Parsing(combine(input_source, source_file), diag_dump_path);
		}
        if (argv_alist.contains("--typecheck")) {
            for (String source_file : source_files) TypecheckerWrapper.Typechecking(combine(input_source, source_file), diag_dump_path, lib_path + "/");
        }
		return;
	}
	
	/**
	 * Combine the input_source and source_file from the argument into a single string, 
	 * that directly points to the location of the file, and would ignore the input_source if
	 * the source_file is an absolute path already.
	 * @param input_souce
	 * @param source_file
	 * @return an absolute path that points to the file
	 */
	public static String combine(String input_source, String source_file) {
		File temp = new File(source_file);
		if (temp.isAbsolute()) {
			return source_file;
		}
		else {
			return input_source + '/' + source_file;
		}
	}
}
