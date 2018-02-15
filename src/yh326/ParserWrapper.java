package yh326;

import java.io.*;

public class ParserWrapper {
	enum PostfixType {
		INTERFACE,
		REGULAR
	};

	public static void Parsing(String sourcePath, String destPath, String filename) {
        String postfix = filename.substring(filename.lastIndexOf(".") + 1);

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

		String filePath = sourcePath + "/" + filename;
		String outputPath = destPath + "/" + filename.substring(filename.lastIndexOf("/") + 1, filename.lastIndexOf(".")) + ".parsed";
		if (System.getProperty("os.name") == "Windows 10") {
			outputPath = outputPath.replace('/', '\\');
		}
		try {
			XiLexer x = new XiLexer(new FileReader(filePath));
			parser p = new parser(x);
			PrintStream o = new PrintStream(new File(outputPath));
	        PrintStream console = System.out;
	        System.setOut(o);
	        p.parse();
	        System.setOut(console);
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
}