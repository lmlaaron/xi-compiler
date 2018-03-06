package yh326;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import yh326.lex.LexerWrapper;
import yh326.parse.ParserWrapper;
import yh326.typecheck.*;

public class Main {
    
    public static void main(String[] argv) {
	    // Note: all ".replace('\\', '/')" is to support Windows 10 path convention.
		ArrayList<String> argvArray = new ArrayList<String> (Arrays.asList(argv));
		
		// Source, output, library file location (relative pwd by default)
		String inputSourcePath = ".";
		String outputPath = ".";
		String libPath = ".";
		
		// Detect options
		if (argvArray.contains("--help")) {
			System.out.println("option --help to show this synopsis.");
			System.out.println("option --lex to show the result from lexical analysis.");
			System.out.println("option --parse to show the result from syntatical analysis.");
			System.out.println("option --typecheck to show the result from type checking.");
			System.out.println("option -sourcepath <path> specifies where to find input source files.");
			System.out.println("option -libpath <path> specify where to find library interface files.");
            System.out.println("option -D <path> specifies where to place generated diagnostic files.");
		}
		try {
		    if (argvArray.contains("-sourcepath")) {
		        inputSourcePath = argv[argvArray.indexOf("-sourcepath") + 1].replace('\\', '/');
		    }
		    if (argvArray.contains("-D")) {
				outputPath = argv[argvArray.indexOf("-D") + 1].replace('\\', '/');
		    }
		    if (argvArray.contains("-libpath")) {
		        libPath = argv[argvArray.indexOf("-libpath") + 1].replace('\\', '/');
		    }
		} catch (IndexOutOfBoundsException e){
            System.out.println("input format incorrect");
            e.printStackTrace();
            System.exit(1);
        }
		
		// Convert to absolute path
		inputSourcePath = Paths.get(inputSourcePath).toAbsolutePath().toString();
		outputPath = Paths.get(outputPath).toAbsolutePath().toString();
		libPath = Paths.get(libPath).toAbsolutePath().toString();
		
		// Add all source files (.xi or .ixi file)
		ArrayList<String> sourceFiles = new ArrayList<String> ();
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].indexOf(".xi") != -1 || argv[i].indexOf(".ixi") != -1) {
                sourceFiles.add(argv[i].replace('\\', '/')); // to support operations in Windows 10
            }
        }
        
        //System.out.println("Source:  " + inputSourcePath);
        //System.out.println("Output:  " + outputPath);
        //System.out.println("Library: " + libPath);
        //System.out.println("Source Files: ");
        //for (String sourceFile : sourceFiles) {
        //    System.out.println(sourceFile);
        //}
        
        // For each file, diagnose.
		for (String sourceFile : sourceFiles) {
		    // Get real source path and output path. For example, if
		    // inputSourcePath is "./a/b" and sourceFile is "c/d.xi", then
		    // the real source path is "./a/b/c". Same for real ourput path.
		    String realInputFile = realPath(inputSourcePath, sourceFile);
		    String realOutputDir = realPath(outputPath, sourceFile);
		    realOutputDir = realOutputDir.substring(0, realOutputDir.lastIndexOf("/"));
		    String fileName = sourceFile.substring(sourceFile.lastIndexOf("/") + 1);
            //System.out.println("Real Source: " + realInputFile);
	        //System.out.println("Real Output: " + realOutputDir);
	        
	        if (argvArray.contains("--lex")) {
			    LexerWrapper.Lexing(realInputFile, realOutputDir, fileName);
			}
		    if (argvArray.contains("--parse")) {
			    ParserWrapper.Parsing(realInputFile, realOutputDir, fileName);
			}
		    if (argvArray.contains("--typecheck") && sourceFile.endsWith(".xi")) {
                TypecheckerWrapper.Typechecking(realInputFile, realOutputDir, fileName,
                        libPath + "/");
            }
        }
		return;
	}
	
	/**
	 * Combine the "path" and sourceFile from the argument into a single string, 
	 * that directly points to the location of the file, and would ignore the "path" if
	 * the sourceFile is an absolute path already.
	 * @param path Absolute path of input source or destination
	 * @param sourceFile Relative or absolute .xi or .ixi file path
	 * @return an absolute path that points to the file
	 */
	public static String realPath(String path, String sourceFile) {
	    if (new File(sourceFile).isAbsolute()) {
			return sourceFile;
		}
		else {
			return Paths.get(path, sourceFile).toString();
		}
	}
}
