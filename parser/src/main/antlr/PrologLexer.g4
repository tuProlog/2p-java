lexer grammar PrologLexer;

options {
  superClass=DynamicLexer;
}

@header {
package alice.tuprolog.parser;
import java.util.*;
import alice.tuprolog.parser.dynamic.*;
import static alice.tuprolog.parser.dynamic.Associativity.*;
}

tokens { VARIABLE }

INTEGER
    : Neg? Digit+
    ;

HEX
    : Neg? Zero 'x' HexDigit+
    ;

OCT
    : Neg? Zero 'o' OctDigit+
    ;

BINARY
    : Neg? Zero 'b' BinDigit+
    ;

FLOAT
    : Neg? Digit+ '.' Digit+ ( 'E' Sign? Digit+ )?
    ;

CHAR
    : Neg? Zero '\'' .
    ;

BOOL
    : 'true'
    | 'fail'
    ;

LPAR
    : '('
    ;

RPAR
    : ')'
    ;

//EMPTY_TUPLE
//    : '(' Ws* ')'
//    ;

LSQUARE
    : '['
    ;

RSQUARE
    : ']'
    ;

EMPTY_LIST
    : LSQUARE Ws* RSQUARE
    ;

LBRACE
    : '{'
    ;

RBRACE
    : '}'
    ;

EMPTY_SET
    : LBRACE Ws* RBRACE
    ;

VARIABLE
    : [_A-Z] [_A-Za-z0-9]*
    ;

SQ_STRING
    : '\'' String '\'' { setText(substring(getText(), 1, -1)); }
    ;

DQ_STRING
    : '"' String '"' { setText(substring(getText(), 1, -1)); }
    ;

COMMA
    : ','
    ;

PIPE
    : '|'
    ;

CUT
    : '!'
    ;

FULL_STOP
    : '.' [ \t]* (COMMENT? FullStopTerminator)
    ;

fragment FullStopTerminator
    : EOF | LINE_COMMENT | [\n\r]+
    ;

WHITE_SPACES
    : Ws+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '%' (~[\r\n])* -> skip
    ;

OPERATOR
    : (Symbols | Atom) { isOperator(getText()) }?
    ;

ATOM
    : (Symbols | Atom) { !isOperator(getText()) }?
    ;

fragment String
    : .*?
    ;

fragment Symbols
    : NotReserved (Symbol* NotReserved)?
    | '!' '!'+
    ;

fragment NotReserved
    : [#$&*+;/\\:<=>?@^~°.] | '-'
    ;

fragment Reserved
    : COMMA | PIPE | LPAR | RPAR | LBRACE | RBRACE | LSQUARE | RSQUARE
    ;

fragment Atom
    : [a-z][A-Za-z0-9_]*
    ;

fragment Symbol
    : NotReserved
    | Reserved
    ;

fragment Ws
    : [ \t\r\n]
    ;

fragment OctDigit
    : [0-7]
    ;

fragment BinDigit
    : [0-1]
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment Digit
    : [0-9]
    ;

fragment Zero
    : '0'
    ;

fragment Sign
    : '+' | '-'
    ;

fragment Neg
    : '-'
    ;