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
    StringBuilder string = new StringBuilder();
    int str_line = 0;
    int str_column = 0;
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
        DONTCARE,
        ERROR
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
StringCharacter = [^\r\n\"\\]
String = "\""{StringCharacter}*"\""
Integer = "0"|[1-9]{Digit}*
Boolean = "true" | "false"
Comment = "//" {InputCharacter}* {LineTerminator}?


/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]
%state STRING, CHARLITERAL

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

  /* string literal */
  \"                             { yybegin(STRING); str_line = yyline; str_column = yycolumn; string.setLength(0);}
  /* character literal */
  \'                             { yybegin(CHARLITERAL); }

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

<STRING> {
  \"                         { yybegin(YYINITIAL);  return new Token(TokenType.STRING, string, str_line, str_column);}
  {StringCharacter}+         {string.append(yytext());}
  
  /* error cases */
  \\.                        {throw new RuntimeException("Illegal escape sequence \""+yytext()+"\""); }
  {LineTerminator}           {throw new RuntimeException("Unterminated string at end of line"); }
} 

<CHARLITERAL> {
  {SingleCharacter}\'        {yybegin(YYINITIAL); return new Token(TokenType.INT, yytext().charAt(0), yyline, yycolumn);}
  
  /* error cases */
  \'                         {yybegin(YYINITIAL); return new Token(TokenType.ERROR,": empty character literal", yyline, yycolumn - 1); }
  \\.                        {throw new RuntimeException("Unterminated character literal at end of line");}
}
