parser grammar PrologParser;

options {
  superClass=DynamicParser;
  tokenVocab=PrologLexer;
}


@header {
package alice.tuprolog.parser;
import java.util.*;
import alice.tuprolog.parser.dynamic.*;
import static alice.tuprolog.parser.dynamic.Associativity.*;
import org.antlr.v4.runtime.RuleContext;
}

@members {
    private static boolean isAnonymous(Token token) {
        return isAnonymous(token.getText());
    }

    private static boolean isAnonymous(String name) {
        return name.length() == 1 && name.charAt(0) == '_';
    }
    
    public static final int P0 = 1201;
    public static final int TOP = 1200;
    public static final int BOTTOM = 0;

    public static final String[] WITH_COMMA = new String[0];
    public static final String[] NO_COMMA = new String[] { "," };
    
    public static final ExpressionContext NO_ROOT = null;
}

singletonTerm
    : term EOF
    ;

singletonExpression
    : expression[P0, WITH_COMMA] EOF
    ;

expression[int priority, String[] disabled]
locals[boolean isTerm, Associativity associativity, int bottom]
    : left=term { $isTerm = true; }
        (
            ( { lookaheadLeq(YFX, $priority, $disabled) }? operators+=op[YFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = YFX; $bottom = $op.priority + 1; }
                (
                    { lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                        right+=expression[$op.priority - 1, $disabled]
                )*

            | { lookaheadLt(XFY, $priority, $disabled) }? operators+=op[XFY]
                right+=expression[$op.priority, $disabled]
                { $associativity = XFY; $bottom = $op.priority; }
                (
                    { lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                        right+=expression[$op.priority, $disabled]
                )*

            | { lookaheadLt(XFX, $priority, $disabled) }? operators+=op[XFX]
                right+=expression[$op.priority - 1, $disabled]
                { $associativity = XFX; $bottom = $op.priority + 1; }

            | { lookaheadLeq(YF, $priority, $disabled) }? operators+=op[YF]
                { $associativity = YF; $bottom = $op.priority; }
                (
                    { lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
                )*

            | { lookaheadLt(XF, $priority, $disabled) }? operators+=op[XF]
                { $associativity = XF; $bottom = $op.priority + 1; }


            ) { $isTerm = false; } ({ lookahead(NON_PREFIX, $priority, $bottom, $disabled) }? outers+=outer[$priority, $bottom, disabled])*
        )?

    | { lookaheadLt(FX, $priority, $disabled) }? operators+=op[FX]
        { $isTerm = false; $associativity = FX; $bottom = $op.priority + 1; }
//        ({ lookaheadEq(FX, $op.priority, $disabled) }? operators+=op[FX])*
        right+=expression[$op.priority - 1, $disabled]
        ({ lookahead(NON_PREFIX, $priority, $bottom, $disabled) }? outers+=outer[$priority, $bottom, disabled])*

    | { lookaheadLeq(FY, $priority, $disabled) }? operators+=op[FY]
        { $isTerm = false; $associativity = FY; $bottom = $op.priority; }
        ({ lookaheadEq(FY, $op.priority, $disabled) }? operators+=op[FY])*
        right+=expression[$op.priority, $disabled] { $associativity = FY; }
        ({ lookahead(NON_PREFIX, $priority, $bottom, $disabled) }? outers+=outer[$priority, $bottom, disabled])*
    ;

outer[int top, int bottom, String[] disabled]
locals[boolean isTerm, Associativity associativity, int newBottom]
    : (
        { lookahead(YFX, $top, $bottom, $disabled) }? operators+=op[YFX]
            { $associativity = YFX; $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]
            (
                { lookaheadEq(YFX, $op.priority, $disabled) }? operators+=op[YFX]
                    right+=expression[$op.priority - 1, $disabled]
            )*

        | { lookahead(XFY, $top, $bottom, $disabled) }? operators+=op[XFY]
            { $associativity = XFY; $newBottom = $op.priority; }
            right+=expression[$op.priority, $disabled]
            (
                { lookaheadEq(XFY, $op.priority, $disabled) }? operators+=op[XFY]
                    right+=expression[$op.priority, $disabled]
            )*

        | { lookahead(XFX, $top, $bottom, $disabled) }? operators+=op[XFX]
            { $associativity = XFX; $newBottom = $op.priority + 1; }
            right+=expression[$op.priority - 1, $disabled]

        | { lookahead(YF, $top, $bottom, $disabled) }? operators+=op[YF]
            { $associativity = YF; $newBottom = $op.priority; }
            (
                { lookaheadEq(YF, $op.priority, $disabled) }? operators+=op[YF]
            )*

        | { lookahead(XF, $top, $bottom, $disabled) }? operators+=op[XF]
            { $associativity = XF; $newBottom = $op.priority + 1; }

    ) ({ lookahead(NON_PREFIX, $top, $newBottom, $disabled) }? outers+=outer[$top, $newBottom, disabled])?
    ;

op[Associativity associativity]
returns[int priority]
    : symbol=OPERATOR { $priority = getOperatorPriority($symbol, $associativity); }
    | symbol=COMMA { $priority = getOperatorPriority($symbol, $associativity); }
    | symbol=PIPE { $priority = getOperatorPriority($symbol, $associativity); }
    ;

term
locals[boolean isNum, boolean isVar, boolean isList, boolean isStruct, boolean isExpr, boolean isSet]
    : LPAR expression[P0, WITH_COMMA] { $isExpr = true; } RPAR
    | number { $isNum = true; }
    | variable { $isVar = true; }
    | structure { $isStruct = true; }
    | list { $isList = true;  }
    | set { $isSet = true;  }
    ;

number
locals[boolean isInt, boolean isReal]
    : integer { $isInt = true; }
    | real { $isReal = true; }
    ;

integer
locals[boolean isHex, boolean isOct, boolean isBin]
    : value=INTEGER
    | value=HEX { $isHex = true; }
    | value=OCT { $isOct = true; }
    | value=BINARY { $isBin = true; }
    ;

real
locals[boolean isHex]
    : value=FLOAT
    | value=HEX_FLOAT{ $isHex = true; }
    ;

variable
locals[boolean isAnonymous]
    : value=VARIABLE { $isAnonymous = isAnonymous($value); }
    ;

structure
locals[int arity = 0, boolean isTruth, boolean isList, boolean isString]
    : functor=BOOL { $isTruth = true; }
    | functor=EMPTY_LIST { $isList = true; }
    | functor=STRING { $isString = true; } (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    | functor=ATOM (LPAR args+=expression[P0, NO_COMMA] { $arity++; } (COMMA args+=expression[P0, NO_COMMA] { $arity++; })* RPAR)?
    ;

list
locals[int length = 0, boolean hasTail]
    : LSQUARE items+=expression[P0, NO_COMMA] { $length++; } (COMMA items+=expression[P0, NO_COMMA] { $length++; })* (PIPE { $hasTail = true; } tail=expression[P0, WITH_COMMA])? RSQUARE
    ;

set
locals[int length = 0, boolean hasTail]
    : LBRACE items+=expression[P0, NO_COMMA] { $length++; } (COMMA items+=expression[P0, NO_COMMA] { $length++; })* RBRACE
    ;