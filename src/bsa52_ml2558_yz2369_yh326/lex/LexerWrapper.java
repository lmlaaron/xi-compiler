package bsa52_ml2558_yz2369_yh326.lex;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.exception.LexingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.gen.sym;

public class LexerWrapper {
    /**
     * 
     * @param realInputFile,
     *            an absolute path to the input file
     * @param realOutputDir,
     *            an absolute path to the output directory
     * @throws LexingException 
     */
    public static void WriteLexingResult(lexer xiLexer, String realOutputFile) throws LexingException {
        try {
            FileWriter writer = new FileWriter(realOutputFile);
            XiSymbol s = (XiSymbol) xiLexer.next_token();
            while (s.sym != sym.EOF) {
                writer.write(s.toString() + "\n");
                if (s.sym == sym.ERROR) {
                    writer.close();
                    throw new LexingException(s.getLine(), s.getColumn(), s.value.toString());
                }
                s = (XiSymbol) xiLexer.next_token();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public static lexer Lexing(String realInputFile) {
        try {
            return new lexer(new FileReader(realInputFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
