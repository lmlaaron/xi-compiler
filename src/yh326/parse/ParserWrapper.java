package yh326.parse;

import yh326.gen.lexer;
import yh326.gen.parser;

import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

public class ParserWrapper {
	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	public static void Parsing(String source_path, String dest_path, String file_path) {
		
		// generate the output postfix
        String postfix = file_path.substring(file_path.lastIndexOf(".") + 1);
		PostfixType type;
        String out_postfix=".parsed";
		if (postfix.equals("ixi")) {
			type = PostfixType.INTERFACE;
            out_postfix=".iparsed";
		}
		else if (postfix.equals("xi")) {
			type = PostfixType.REGULAR;
            out_postfix=".parsed";
		}
		else {
			System.out.println("file postfix isn't correct");
			System.exit(1);
		}
		
		// generate the complete input and output path
		String absolute_file_path;
		String absolute_output_path;
		File temp = new File(file_path);
		if (temp.isAbsolute()) {
			absolute_file_path = file_path;
		}
		else {
			absolute_file_path = source_path + "/" + file_path;
		}
		absolute_output_path = dest_path + "/" + file_path.substring(file_path.lastIndexOf("/") + 1, file_path.lastIndexOf(".")) + out_postfix;
		
		// parse
		try {
			lexer x = new lexer(new FileReader(absolute_file_path));
			parser p = new parser(x);
			System.setOut(new PrintStream(new File(absolute_output_path)));
			p.parse();
			System.setOut(System.out);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		return;
	}
}
