%%

%public
%class XiLexer
%type Token
%function nextToken

%unicode
%pack

%{
    enum TokenType {
        USE,
	IF,
        WHILE,
        ELSE,
        RETURN,
	ID,
	INT,
	BOOL,
        SEPARATOR,
        OPERATOR,
        DOT,
        LENGTH
    }
    class Token {
	TokenType type;
	Object attribute;
        int line;
        int column;
	Token(TokenType tt, int Line, int Column) {
	    type = tt; attribute = null; line = Line; column = Column;
	}
	Token(TokenType tt, Object attr,int Line, int Column) {
	    type = tt; attribute = attr; line= Line; column = Column;
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
Identifier = {Letter}({Digit}|{Letter}|_)*
Integer = "0"|"-"?[1-9]{Digit}*
Boolean = "true" | "false"
Comment = "//" {InputCharacter}* {LineTerminator}?

%%


<YYINITIAL> {
  {Whitespace}  { /* ignore */ }
  "use"         { return new Token(TokenType.USE, yyline, yycolumn); }
  "if"          { return new Token(TokenType.IF, yyline, yycolumn); }
  "while"       { return new Token(TokenType.WHILE, yyline, yycolumn); }
  "else"        { return new Token(TokenType.ELSE, yyline, yycolumn); }
  "return"      { return new Token(TokenType.RETURN, yyline, yycolumn);}
  "length"      { return new Token(TokenType.LENGTH, yyline, yycolumn);}
  
  {Integer}     { return new Token(TokenType.INT,
  				 Integer.parseInt(yytext()), yyline, yycolumn); }
  {Boolean}     { return new Token(TokenType.BOOL, yytext(), yyline, yycolumn); }
  {Identifier}  { return new Token(TokenType.ID, yytext(), yyline, yycolumn); }

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

