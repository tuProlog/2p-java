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
    : '0'
    | [1-9] (Digits? | '_' + Digits)
    ;

HEX
    : '0' [xX] [0-9a-fA-F] ([0-9a-fA-F_]* [0-9a-fA-F])?
    ;

OCT
    : '0' '_'* [0-7] ([0-7_]* [0-7])?
    ;

BINARY
    : '0' [bB] [01] ([01_]* [01])?
    ;

FLOAT
    : (Digits '.' Digits? | '.' Digits) ExponentPart? [fFdD]?
    | Digits (ExponentPart [fFdD]? | [fFdD])
    ;

HEX_FLOAT
    : '0' [xX] (HexDigits '.'? | HexDigits? '.' HexDigits) [pP] [+-]? Digits [fFdD]?
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
    : '[' Ws* ']'
    ;

LBRACE
    : '{'
    ;

RBRACE
    : '}'
    ;

//EMPTY_SET
//    : '{' Ws* '}'
//    ;

VARIABLE
    : [_A-Z] [_A-Za-z0-9]*
    ;

STRING
    : ('"' .*? '"' | '\'' .*? '\'') { setText(substring(getText(), 1, -1)); }
    ;

COMMA
    : ','
    ;

PIPE
    : '|'
    ;

WHITE_SPACES
    : Ws+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
    ;

LINE_COMMENT
    : '%' ~[\r\n]* -> channel(HIDDEN)
    ;

OPERATOR
    : (Symbols | Atom) { isOperator(getText()) }?
    ;

ATOM
    : (Symbols | Atom) { !isOperator(getText()) }?
    ;

fragment Atom
    : [a-z][A-Za-z0-9_]*
    ;

fragment Symbols
    : '!' | [#$&*+-.;/\\:<=>?@^~°][#$&*+-.;/\\:<=>?@^~°!|]*
    ;

fragment Ws
    : [ \t\r\n]
    ;

fragment ExponentPart
    : [eE] [+-]? Digits
    ;

fragment HexDigits
    : HexDigit ((HexDigit | '_')* HexDigit)?
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment Digits
    : [0-9] ([0-9_]* [0-9])?
    ;