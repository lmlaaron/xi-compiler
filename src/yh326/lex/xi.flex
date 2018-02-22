package yh326.gen;

import yh326.lex.XiSymbol;

import java_cup.runtime.Symbol;

@SuppressWarnings("unused")

%%

%public
%class lexer
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
        return new XiSymbol(type, null, value, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, String typeString, Object value) {
        return new XiSymbol(type, typeString, value, yyline+1, yycolumn+1);
    }

    private Symbol symbol(int type, Object value, int line, int column) {
        return new XiSymbol(type, null, value, line+1, column+1);
    }

    private Symbol symbol(int type, String typeString, Object value, int line, int column) {
        return new XiSymbol(type, typeString, value, line+1, column+1);
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
Integer = "0"|[1-9]{Digit}*
Comment = "//" {InputCharacter}* {LineTerminator}?

%state YYSTRING, CHARLITERAL

%%

<YYINITIAL> {
  "true"            { return symbol(TRUE,          yytext()); }
  "false"           { return symbol(FALSE,         yytext()); }
  "int"             { return symbol(INT,           yytext()); }
  "bool"            { return symbol(BOOL,          yytext()); }

  "if"              { return symbol(IF,            yytext()); }
  "else"            { return symbol(ELSE,          yytext()); }
  "while"           { return symbol(WHILE,         yytext()); }
  // BREAK ???
  "return"          { return symbol(RETURN,        yytext()); }
  "use"             { return symbol(USE,           yytext()); }
  "length"          { return symbol(LENGTH,        yytext()); }
  "+"               { return symbol(PLUS,          yytext()); }
  "-"               { return symbol(MINUS,         yytext()); }
  "*"               { return symbol(TIMES,         yytext()); }
  "*>>"             { return symbol(TIMES_SHIFT,   yytext()); }
  "/"               { return symbol(DIVIDE,        yytext()); }
  "%"               { return symbol(MODULO,        yytext()); }
  "!"               { return symbol(NOT,           yytext()); }
  "<"               { return symbol(LT,            yytext()); }
  "<="              { return symbol(LEQ,           yytext()); }
  ">"               { return symbol(GT,            yytext()); }
  ">="              { return symbol(GEQ,           yytext()); }
  "&"               { return symbol(AND,           yytext()); }
  "|"               { return symbol(OR,            yytext()); }
  "=="              { return symbol(EQUAL,         yytext()); }
  "!="              { return symbol(NOT_EQUAL,     yytext()); }
  "="               { return symbol(GETS,          yytext()); }
  "["               { return symbol(OPEN_BRACKET,  yytext()); }
  "]"               { return symbol(CLOSE_BRACKET, yytext()); }
  "("               { return symbol(OPEN_PAREN,    yytext()); }
  ")"               { return symbol(CLOSE_PAREN,   yytext()); }
  "{"               { return symbol(OPEN_BRACE,    yytext()); }
  "}"               { return symbol(CLOSE_BRACE,   yytext()); }
  ":"               { return symbol(COLON,         yytext()); }
  ","               { return symbol(COMMA,         yytext()); }
  ";"               { return symbol(SEMICOLON,     yytext()); }
  "_"               { return symbol(UNDERSCORE,    yytext()); }

  "\""              { yybegin(YYSTRING); column = yycolumn; string.setLength(0);}
  \'                { yybegin(CHARLITERAL); column = yycolumn; }

  {Identifier}      { return symbol(IDENTIFIER,      "id",      yytext()); }
  {Integer}         { return symbol(INTEGER_LITERAL, "integer", yytext()); }
  {Whitespace}      { /* ignore */ }
  {Comment}         { /* ignore */ }

}

<YYSTRING> {
  \"                   { yybegin(YYINITIAL); return symbol(STRING_LITERAL, "string", "" + string, yyline, column);}
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(ERROR, ": Unterminated string at end of line", yyline, column); }
  \\x{HexDigit}{2}     { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\u{HexDigit}{4}     { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\.|.                { string.append(yytext());}
}

<CHARLITERAL> {
  \'                   { yybegin(YYINITIAL); return symbol(ERROR, "error: empty character literal", yyline, column); }
  {LineTerminator}     { yybegin(YYINITIAL); return symbol(ERROR, "error: unterminated character literal at end of line", yyline, column);}
  \\x{HexDigit}{2}\'   { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, "character", "" + Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, column); }
  \\x{HexDigit}{4}\'   { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, "character", "" + Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, column); }
  (\\.|.)\'            { yybegin(YYINITIAL); return symbol(CHARACTER_LITERAL, "character", yytext().substring(0, yylength()-1), yyline, column);}
  .[^\']+\'            { yybegin(YYINITIAL); return symbol(ERROR, "error: invalid character literal: \'" + yytext(), yyline, column); }
}

  [^]                  { return symbol(ERROR, "error: unrecognized character: "+yytext()); }
  <<EOF>>              { return symbol(EOF, yytext()); }
