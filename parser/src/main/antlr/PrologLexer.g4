lexer grammar PrologLexer;

options {
  superClass=DynamicLexer;
}

@header {
package alice.tuprolog.parser;
import java.util.*;
import alice.tuprolog.parser.dynamic.*;
import static alice.tuprolog.parser.dynamic.Associativity.*;
import static alice.tuprolog.parser.dynamic.StringType.*;
}

tokens { VARIABLE }

INTEGER
    : Digit+
    ;

HEX
    : Zero [xX] HexDigit+
    ;

OCT
    : Zero [oO] OctDigit+
    ;

BINARY
    : Zero [bB] BinDigit+
    ;

SIGN
    : '+' | '-'
    ;

FLOAT
    : Digit+ '.' Digit+ ( [eE] SIGN? Digit+ )?
    ;

CHAR
    : Zero '\'' .
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
    : '\'' ((~[\n']) | Escapable | DoubleSQ)* '\'' { setText(escape(unquote(getText()), SINGLE_QUOTED)); }
    ;

DQ_STRING
    : '"' ((~[\n"]) | Escapable | DoubleDQ)* '"' { setText(escape(unquote(getText()), DOUBLE_QUOTED)); }
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

fragment Symbols
    : NotReserved (Symbol* NotReserved)?
    | '!' '!'+
    ;

fragment Escapable
    : '\\'
        ( [abfnrtv'`"]
        | '\\'
        | ('\r'? '\n')
        | (OctDigit+ '\\')
        | (HexDigit+ '\\')
        )
    ;

fragment DoubleSQ
    : '\'\''
    ;

fragment DoubleDQ
    : '""'
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