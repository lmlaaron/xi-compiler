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
        STRING,
        LENGTH,
        DUMMY,
        ERROR,
        CHARACTER
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
  "use"             { return new Token(TokenType.USE,    yyline, yycolumn); }
  "if"              { return new Token(TokenType.IF,     yyline, yycolumn); }
  "while"           { return new Token(TokenType.WHILE,  yyline, yycolumn); }
  "else"            { return new Token(TokenType.ELSE,   yyline, yycolumn); }
  "return"          { return new Token(TokenType.RETURN, yyline, yycolumn); }
  "length"          { return new Token(TokenType.LENGTH, yyline, yycolumn); }
  "int"             { return new Token(TokenType.INT,    yyline, yycolumn); }
  "bool"            { return new Token(TokenType.BOOL,   yyline, yycolumn); }
  
  {Integer}         { return new Token(TokenType.INTEGER,   yytext(), yyline, yycolumn); }
  {Boolean}         { return new Token(TokenType.BOOLEAN,   yytext(), yyline, yycolumn); }
  {Identifier}      { return new Token(TokenType.ID,        yytext(), yyline, yycolumn); }
  {Dummy}           { return new Token(TokenType.DUMMY,     yytext(), yyline, yycolumn); }
  {Separator}       { return new Token(TokenType.SEPARATOR, yytext(), yyline, yycolumn); }
  {Operator}        { return new Token(TokenType.OPERATOR,        yytext(), yyline, yycolumn); }
  {Comment}         {/* ignore */}

  "\""              { yybegin(YYSTRING); str_line = yyline; str_column = yycolumn; string.setLength(0); string.append(yytext());}
  \'                { yybegin(CHARLITERAL); }
}

<YYSTRING> {
  \"                   { string.append(yytext()); yybegin(YYINITIAL); return new Token(TokenType.STRING, string, str_line, str_column);}
  {LineTerminator}     { yybegin(YYINITIAL); return new Token(TokenType.ERROR, ": Unterminated string at end of line", str_line, str_column); }
  \\x{HexDigit}{1,4}   { string.append((char) Integer.parseInt(yytext().substring(2), 16)); }
  \\.|.                { string.append(yytext());}
} 

<CHARLITERAL> {
  \'                   { yybegin(YYINITIAL); return new Token(TokenType.ERROR, ": empty character literal", yyline, yycolumn - 1); }
  {LineTerminator}     { yybegin(YYINITIAL); return new Token(TokenType.ERROR, ": Unterminated character literal at end of line", yyline, yycolumn -1);}
  \\x{HexDigit}{1,4}\' { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, (char) Integer.parseInt(yytext().substring(2, yylength()-1), 16), yyline, yycolumn-1); }
  (\\.|.)\'            { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, yytext().substring(0, yylength()-1), yyline, yycolumn-1);}
  .[^\']+\'            { yybegin(YYINITIAL); return new Token(TokenType.ERROR, ": invalid character literal: \'" + yytext(), yyline, yycolumn - 1); }
}

  [^]               { return new Token(TokenType.ERROR, ": Unrecognized character: "+yytext(), yyline, yycolumn); }

