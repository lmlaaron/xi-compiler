package yh326;

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
			for (String source_file : source_files) LexerWrapper.Lexing(input_source, diag_dump_path, source_file);
		}
		if (argv_alist.contains("--parse")) {
			for (String source_file : source_files) ParserWrapper.Parsing(input_source, diag_dump_path, source_file);
		}
        if (argv_alist.contains("--typecheck")) {
            for (String source_file : source_files) TypecheckerWrapper.Typechecking(input_source, diag_dump_path, source_file);
        }
		return;
	}
}
