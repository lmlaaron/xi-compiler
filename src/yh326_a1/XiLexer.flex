import java_cup.runtime.*;

%%

%public
%class XiLexer
%implements sym
%function next_token

%unicode

%line
%column

%cup
%cupdebug

%{
    StringBuilder string = new StringBuilder();
    int str_line = 0;
    int str_column = 0;

    private Symbol symbol(int type) {
        return new XiSymbol(type, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, int line, int column) {
        return new XiSymbol(type, line+1, column+1);
    }

    private Symbol symbol(int type, Object value) {
        return new XiSymbol(type, yyline+1, yycolumn+1, value);
    }

    private Symbol symbol(int type, Object value, int line, int column) {
        return new XiSymbol(type, line+1, column+1, value);
    }
%}


/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
HexDigit =[0-9a-fA-F]
Identifier = {Letter}({Digit}|{Letter}|_|')*
Dummy = _
Integer = "0"|[1-9]{Digit}*
Boolean = "true" | "false"
Comment = "//" {InputCharacter}* {LineTerminator}?
Separator = [\(\)\{\}\[\];:,\.]
Operator = (-|\!|\*|\*>>|\/|%|\+|<|<=|>=|>|==|\!=|&|\||=)

%state YYSTRING, CHARLITERAL

%%

<YYINITIAL> {
  {Whitespace}  { /* ignore */ }
  "use"             { return symbol(sym.USE,       yytext()); }
  "if"              { return symbol(sym.IF,        yytext()); }
  "while"           { return symbol(sym.WHILE,     yytext()); }
  "else"            { return symbol(sym.ELSE,      yytext()); }
  "return"          { return symbol(sym.RETURN,    yytext()); }
  "length"          { return symbol(sym.LENGTH,    yytext()); }
  "int"             { return symbol(sym.INT,       yytext()); }
  "bool"            { return symbol(sym.BOOL,      yytext()); }

  {Integer}         { return symbol(sym.INTEGER,   yytext()); }
  {Boolean}         { return symbol(sym.BOOLEAN,   yytext()); }
  {Identifier}      { return symbol(sym.ID,        yytext()); }
  {Dummy}           { return symbol(sym.DUMMY,     yytext()); }
  {Separator}       { return symbol(sym.SEPARATOR, yytext()); }
  {Operator}        { return symbol(sym.OPERATOR,  yytext()); }
  {Comment}         {/* ignore */}

  "\""              { yybegin(YYSTRING); str_line = yyline; str_column = yycolumn; string.setLength(0); string.append(yytext());}
  \'                { yybegin(CHARLITERAL); }
}

<YYSTRING> {
  \"                   { string.append(yytext()); yybegin(YYINITIAL); return symbol(sym.STRING, string, str_line, str_column);}
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(sym.ERROR, ": Unterminated string at end of line", str_line, str_column); }
  \\x{HexDigit}{1,4}   { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\.|.                { string.append(yytext());}
}

<CHARLITERAL> {
  \'                   { yybegin(YYINITIAL); return symbol(sym.ERROR, ": empty character literal", yyline, yycolumn - 1); }
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(sym.ERROR, ": Unterminated character literal at end of line", yyline, yycolumn -1);}
  \\x{HexDigit}{1,4}\' { yybegin(YYINITIAL); return symbol(sym.CHARACTER, (char) Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, yycolumn-1); }
  (\\.|.)\'            { yybegin(YYINITIAL); return symbol(sym.CHARACTER, yytext().substring(0, yylength()-1), yyline, yycolumn-1);}
  .[^\']+\'            { yybegin(YYINITIAL); return symbol(sym.ERROR, ": invalid character literal: \'" + yytext(), yyline, yycolumn - 1); }
}

  [^]               { return symbol(sym.ERROR, ": Unrecognized character: "+yytext()); }
