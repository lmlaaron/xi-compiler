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
    int column = 0;

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
  "use"             { return symbol(USE,       yytext()); }
  "if"              { return symbol(IF,        yytext()); }
  "while"           { return symbol(WHILE,     yytext()); }
  "else"            { return symbol(ELSE,      yytext()); }
  "return"          { return symbol(RETURN,    yytext()); }
  "length"          { return symbol(LENGTH,    yytext()); }
  "int"             { return symbol(INT,       yytext()); }
  "bool"            { return symbol(BOOL,      yytext()); }

  {Boolean}         { return symbol(BOOLEAN,   yytext()); }
  {Dummy}           { return symbol(DUMMY,     yytext()); }
  {Separator}       { return symbol(SEPARATOR, yytext()); }
  {Operator}        { return symbol(OPERATOR,  yytext()); }

  {Integer}         { return symbol(INTEGER,   "integer " + yytext()); }
  {Identifier}      { return symbol(ID,        "id " + yytext()); }
  {Comment}         {/* ignore */}

  "\""              { yybegin(YYSTRING); column = yycolumn; string.setLength(0);}
  \'                { yybegin(CHARLITERAL); column = yycolumn; }
}

<YYSTRING> {
  \"                   { yybegin(YYINITIAL); return symbol(STRING, "string " + string, yyline, column);}
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(ERROR, ": Unterminated string at end of line", yyline, column); }
  \\x{HexDigit}{1,4}   { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\.|.                { string.append(yytext());}
}

<CHARLITERAL> {
  \'                   { yybegin(YYINITIAL); return symbol(ERROR, "error: empty character literal", yyline, column); }
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(ERROR, "error: Unterminated character literal at end of line", yyline, column);}
  \\x{HexDigit}{1,4}\' { yybegin(YYINITIAL); return symbol(CHARACTER, "character " + (char) Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, column); }
  (\\.|.)\'            { yybegin(YYINITIAL); return symbol(CHARACTER, "character " + yytext().substring(0, yylength()-1), yyline, column);}
  .[^\']+\'            { yybegin(YYINITIAL); return symbol(ERROR, "error: invalid character literal: \'" + yytext(), yyline, column); }
}

  [^]               { return symbol(ERROR, "error: Unrecognized character: "+yytext()); }
<<EOF>>                          { return symbol(EOF); }
