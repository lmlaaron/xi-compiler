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
        DUMMYIDENTIFIER,
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
    private int value() {
         int r = 0;

         for (int k = zzMarkedPos-4; k < zzMarkedPos; k++) {
           int c = zzBuffer[k];

           if (c >= 'a') 
             c-= 'a'-10;
           else if (c >= 'A')
             c-= 'A'-10;
           else
             c-= '0';

           r <<= 4;
           r += c;
         }
         return r;
   }
%}


/* main character classes */
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]

Whitespace = [ \t\f\r\n]
Letter = [a-zA-Z]
Digit = [0-9]
Identifier = {Letter}({Digit}|{Letter}|_|')*
DummyIdentifier = _
//StringCharacter = [^\r\n\"\\]
//String = "\""{StringCharacter}*"\""
Integer = "0"|[1-9]{Digit}*
Boolean = "true" | "false"
Comment = "//" {InputCharacter}* {LineTerminator}?


/* string and character literals */
StringCharacter = [^\r\n\"\\]
SingleCharacter = [^\r\n\'\\]

//UnicodeEscape = {UnicodeMarker} {HexDigit} 
//UnicodeMarker ="\\x"
HexDigit =[0-9a-fA-F]

%state YYSTRING, CHARLITERAL

%%


<YYINITIAL> {
  {Whitespace}  { /* ignore */ }
  "use"             { return new Token(TokenType.USE, yyline, yycolumn); }
  "if"              { return new Token(TokenType.IF, yyline, yycolumn); }
  "while"           { return new Token(TokenType.WHILE, yyline, yycolumn); }
  "else"            { return new Token(TokenType.ELSE, yyline, yycolumn); }
  "return"          { return new Token(TokenType.RETURN, yyline, yycolumn); }
  "length"          { return new Token(TokenType.LENGTH, yyline, yycolumn); }
  "int"             { return new Token(TokenType.INT, yyline, yycolumn); }
  "bool"            { return new Token(TokenType.BOOL, yyline, yycolumn); }
  
  {Integer}         { return new Token(TokenType.INTEGER, Long.parseUnsignedLong(yytext()), yyline, yycolumn); }
  {Boolean}         { return new Token(TokenType.BOOLEAN, yytext(), yyline, yycolumn); }
  {Identifier}      { return new Token(TokenType.ID, yytext(), yyline, yycolumn); }
  //{String}        { return new Token(TokenType.STRING, yytext(), yyline, yycolumn); }
  {DummyIdentifier} { return new Token(TokenType.DUMMYIDENTIFIER, yytext(), yyline, yycolumn); }

  /* string literal */
  "\""                             { yybegin(YYSTRING); str_line = yyline; str_column = yycolumn; string.setLength(0); string.append(yytext());}
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

<YYSTRING> {
  "\x64"                      {;}
  "\""                         {string.append(yytext()); yybegin(YYINITIAL);  return new Token(TokenType.STRING, string, str_line, str_column);}
  {StringCharacter}+         {string.append(yytext());}

  /*escape sequences*/
  "\\b"                          { string.append( "\\b" ); }
  "\\t"                          { string.append( "\\t" ); }
  "\\n"                          { string.append( "\\n" ); }
  "\\f"                          { string.append( "\\f" ); }
  "\\r"                          { string.append( "\\r" ); }
  "\\\""                         { string.append( "\\\"" ); }
  "\\'"                          { string.append( "\\\'" ); }
  "\\\\"                         { string.append( "\\\\" ); }
  \\x{HexDigit}?{HexDigit}    { char val = (char) Integer.parseInt(yytext().substring(2),16); string.append(val); }
 
  /* error cases */
  {LineTerminator}           { yybegin(YYINITIAL); return new Token(TokenType.ERROR, ": Unterminated string at end of line", str_line, str_column); }
} 

<CHARLITERAL> {
  {SingleCharacter}\'        { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, yytext().charAt(0), yyline, yycolumn-1);}

  /*escape sequences*/
  "\\b"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\b', yyline, yycolumn-1); }    
  "\\t"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\t', yyline, yycolumn-1); } 
  "\\n"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\n', yyline, yycolumn-1); } 
  "\\f"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\f', yyline, yycolumn-1); } 
  "\\r"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\r', yyline, yycolumn-1); } 
  "\\\""\'                   { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\"', yyline, yycolumn-1);   } 
  "\\'"\'                    { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\'', yyline, yycolumn-1);   } 
  "\\\\"\'                   { yybegin(YYINITIAL); return new Token(TokenType.CHARACTER, '\\', yyline, yycolumn-1);   } 
  \\x{HexDigit}+\'    { yybegin(YYINITIAL); 
                        int n = Integer.parseInt(yytext().substring(2, yylength()-1),16); 
                        char val = (char) n; 
                        if (n < 32) { return new Token(TokenType.ERROR, ": Non printable character "+ yytext(), yyline, yycolumn-1); }
                        else return new Token(TokenType.CHARACTER, val, yyline, yycolumn-1); }
 
  /* error cases */
  \'                         { yybegin(YYINITIAL); return new Token(TokenType.ERROR,": empty character literal", yyline, yycolumn - 1); }
  \\.                        { yybegin(YYINITIAL); return new Token(TokenType.ERROR,": Unterminated character literal at end of line", yyline, yycolumn -1);}
}

[^]                          { yybegin(YYINITIAL); return new Token(TokenType.ERROR,": Unrecognized character"+yytext(), yyline, yycolumn); }
