package bsa52_ml2558_yz2369_yh326.lex;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import bsa52_ml2558_yz2369_yh326.exception.LexingException;
import bsa52_ml2558_yz2369_yh326.gen.lexer;
import bsa52_ml2558_yz2369_yh326.gen.sym;
import bsa52_ml2558_yz2369_yh326.util.Settings;

public class LexerWrapper {

    public static lexer Lexing(FileReader file, String outputFile) throws LexingException {
        lexer xiLexer = new lexer(file);
        if (Settings.lex)
            LexerWrapper.WriteLexingResult(xiLexer, outputFile + ".lexed");
        return xiLexer;
    }

    public static void WriteLexingResult(lexer xiLexer, String outputFile) throws LexingException {
        try {
            FileWriter writer = new FileWriter(outputFile);
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

}
