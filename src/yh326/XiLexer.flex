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

    private Symbol symbol(int type, Object value) {
        return new XiSymbol(type, value, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value, int line, int column) {
        return new XiSymbol(type, value, line+1, column+1);
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

%state YYSTRING, CHARLITERAL

%%

<YYINITIAL> {
  {Whitespace}  { /* ignore */ }
  "use"             { return symbol(USE,              yytext()); }
  "if"              { return symbol(IF,               yytext()); }
  "while"           { return symbol(WHILE,            yytext()); }
  "else"            { return symbol(ELSE,             yytext()); }
  "return"          { return symbol(RETURN,           yytext()); }
  "length"          { return symbol(LENGTH,           yytext()); }
  "int"             { return symbol(INT,              yytext()); }
  "bool"            { return symbol(BOOL,             yytext()); }

  {Boolean}         { return symbol(BOOLEAN,          yytext()); }
  {Integer}         { return symbol(INTEGER, "integer " + yytext()); }
  "["               { return symbol(OPEN_BRACKET,     yytext()); }
  "]"               { return symbol(CLOSE_BRACKET,    yytext()); }
  "("               { return symbol(OPEN_PAREN,       yytext()); }
  ")"               { return symbol(CLOSE_PAREN,      yytext()); }
  "{"               { return symbol(OPEN_BRACE,       yytext()); }
  "}"               { return symbol(CLOSE_BRACE,      yytext()); }
  ","               { return symbol(COMMA,            yytext()); }
  ";"               { return symbol(SEMICOLON,        yytext()); }
  ":"               { return symbol(COLON,            yytext()); }
  "="               { return symbol(ASSIGN,           yytext()); }
  "-"               { return symbol(SUBTRACTION,      yytext()); }
  "!"               { return symbol(BOOLEAN_NEGATION, yytext()); }
  "*"               { return symbol(MULTIPLICATION,   yytext()); }
  "*>>"             { return symbol(HIGH_MULTI,       yytext()); }
  "/"               { return symbol(DIVISION,         yytext()); }
  "%"               { return symbol(REMAINDER,        yytext()); }
  "+"               { return symbol(ADDITION,         yytext()); }
  "<"               { return symbol(LT,               yytext()); }
  "<="              { return symbol(LEQ,              yytext()); }
  ">="              { return symbol(GEQ,              yytext()); }
  ">"               { return symbol(GT,               yytext()); }
  "=="              { return symbol(EQUAL,            yytext()); }
  "!="              { return symbol(NOT_EQUAL,        yytext()); }
  "&"               { return symbol(LOGICAL_AND,      yytext()); }
  "|"               { return symbol(LOGICAL_OR,       yytext()); }

  "_"               { return symbol(UNDERSCORE,       yytext()); }
  {Identifier}      { return symbol(IDENTIFIER, "id " + yytext()); }
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
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(ERROR, "error: unterminated character literal at end of line", yyline, column);}
  \\x{HexDigit}{1,4}\' { yybegin(YYINITIAL); return symbol(CHARACTER, "character " + (char) Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, column); }
  (\\.|.)\'            { yybegin(YYINITIAL); return symbol(CHARACTER, "character " + yytext().substring(0, yylength()-1), yyline, column);}
  .[^\']+\'            { yybegin(YYINITIAL); return symbol(ERROR, "error: invalid character literal: \'" + yytext(), yyline, column); }
}

  [^]                  { return symbol(ERROR, "error: unrecognized character: "+yytext()); }
  <<EOF>>              { return symbol(EOF, yytext()); }
