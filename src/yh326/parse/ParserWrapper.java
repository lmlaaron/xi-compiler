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

	public static void Parsing(String sourcePath, String destPath, String filename) {
        String postfix = filename.substring(filename.lastIndexOf(".") + 1);

		//PostfixType type;

        String out_postfix=".parsed";
		if (postfix.equals("ixi")) {
			//type = PostfixType.INTERFACE;
            out_postfix=".iparsed";
		}
		else if (postfix.equals("xi")) {
			//type = PostfixType.REGULAR;
            out_postfix=".parsed";
		}
		else {
			System.out.println("file postfix isn't correct");
			System.exit(1);
		}

		String filePath = sourcePath + "/" + filename;
		String outputPath = destPath + "/" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + out_postfix;
		if (System.getProperty("os.name") == "Windows 10") {
			outputPath = outputPath.replace('/', '\\');
		}
		try {
			lexer x = new lexer(new FileReader(filePath));
			parser p = new parser(x);
			System.setOut(new PrintStream(new File(outputPath)));
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
