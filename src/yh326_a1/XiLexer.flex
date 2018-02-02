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
	Token(TokenType tt) {
	    type = tt; attribute = null;
	}
	Token(TokenType tt, Object attr) {
	    type = tt; attribute = attr;
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
  "use"         { return new Token(TokenType.USE); }
  "if"          { return new Token(TokenType.IF); }
  "while"       { return new Token(TokenType.WHILE); }
  "else"        { return new Token(TokenType.ELSE); }
  "return"      { return new Token(TokenType.RETURN);}
  "length"      { return new Token(TokenType.LENGTH);}
  
  {Integer}     { return new Token(TokenType.INT,
  				 Integer.parseInt(yytext())); }
  {Boolean}     { return new Token(TokenType.BOOL, yytext()); }
  {Identifier}  { return new Token(TokenType.ID, yytext()); }

  /* separators */
  "("                            { return new Token(TokenType.SEPARATOR, yytext()); }
  ")"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  "{"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  "}"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  "["                            { return new Token(TokenType.SEPARATOR, yytext()); }
  "]"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  ";"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  ":"                            { return new Token(TokenType.SEPARATOR, yytext()); }
  ","                            { return new Token(TokenType.SEPARATOR, yytext()); }
  "."                            { return new Token(TokenType.SEPARATOR, yytext()); } 
  
  /* operators */
  "-"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "!"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "*"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "*>>"                      { return new Token(TokenType.OPERATOR, yytext()); } 
  "/"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "%"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "+"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "<"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "<="                       { return new Token(TokenType.OPERATOR, yytext()); } 
  ">="                       { return new Token(TokenType.OPERATOR, yytext()); } 
  ">"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "=="                       { return new Token(TokenType.OPERATOR, yytext()); } 
  "!="                       { return new Token(TokenType.OPERATOR, yytext()); } 
  "&"                        { return new Token(TokenType.OPERATOR, yytext()); } 
  "|"                        { return new Token(TokenType.OPERATOR, yytext()); }  

  /* comments */
  {Comment}                  {/* ignore */}
}

