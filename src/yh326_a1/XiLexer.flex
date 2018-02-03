%%

%public
%class XiLexer
%type Token
%function nextToken

%unicode
%pack
%line
%column

%{
    enum TokenType {
        USE,
	IF,
        WHILE,
        ELSE,
        RETURN,
	ID,
	INT,
        INTEGER,
	BOOL,
        BOOLEAN,
        SEPARATOR,
        OPERATOR,
        DOT,
        STRING,
        LENGTH,
        DONTCARE
    }
    class Token {
	TokenType type;
	Object attribute;
        int line;
        int column;
	Token(TokenType tt, int Line, int Column) {
	    type = tt; attribute = null; line = Line + 1; column = Column + 1;
	}
	Token(TokenType tt, Object attr,int Line, int Column) {
	    type = tt; attribute = attr; line= Line + 1; column = Column + 1;
	}
	public String toString() {
	    return "" + type + "(" + attribute + ")";
	}
    }
%}

/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Digit}|{Letter}|_)*(')*
Dontcare = _
StringCharacter = [^\r\"\\]
String = "\""{StringCharacter}*"\""
Integer = "0"|[1-9]{Digit}*
Boolean = "true" | "false"
Comment = "//" {InputCharacter}* {LineTerminator}?

%%


<YYINITIAL> {
  {Whitespace}  { /* ignore */ }
  "use"         { return new Token(TokenType.USE, yyline, yycolumn); }
  "if"          { return new Token(TokenType.IF, yyline, yycolumn); }
  "while"       { return new Token(TokenType.WHILE, yyline, yycolumn); }
  "else"        { return new Token(TokenType.ELSE, yyline, yycolumn); }
  "return"      { return new Token(TokenType.RETURN, yyline, yycolumn); }
  "length"      { return new Token(TokenType.LENGTH, yyline, yycolumn); }
  "int"         { return new Token(TokenType.INT, yyline, yycolumn); }
  "bool"        { return new Token(TokenType.BOOL, yyline, yycolumn); }
  
  {Integer}     { return new Token(TokenType.INTEGER,
  				 Integer.parseInt(yytext()), yyline, yycolumn); }
  {Boolean}     { return new Token(TokenType.BOOLEAN, yytext(), yyline, yycolumn); }
  {Identifier}  { return new Token(TokenType.ID, yytext(), yyline, yycolumn); }
  {String}      { return new Token(TokenType.STRING, yytext(), yyline, yycolumn); }
  {Dontcare}    { return new Token(TokenType.DONTCARE, yytext(), yyline, yycolumn); }

  /* separators */
  "("                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  ")"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  "{"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  "}"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  "["                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  "]"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  ";"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  ":"                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  ","                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  "."                            { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); } 
  
  /* operators */
  "-"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); }
  "="                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "!"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "*"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "*>>"                      { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "/"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "%"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "+"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "<"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "<="                       { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  ">="                       { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  ">"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "=="                       { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "!="                       { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "&"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); } 
  "|"                        { return new Token(TokenType.OPERATOR, yytext(), yyline, yycolumn); }  

  /* comments */
  {Comment}                  {/* ignore */}
}

