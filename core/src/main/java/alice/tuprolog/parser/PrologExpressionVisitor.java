package alice.tuprolog.parser;

import alice.tuprolog.Int;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import alice.tuprolog.parser.PrologParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static alice.tuprolog.parser.dynamic.Associativity.*;

public class PrologExpressionVisitor extends PrologParserBaseVisitor<Term> {

    private static final Pattern BIN_PREFIX = Pattern.compile("(0b)|(0B)");
    private static final Pattern HEX_PREFIX = Pattern.compile("(0b)|(0B)");
    private static final Pattern OCT_PREFIX = Pattern.compile("(0b)|(0B)");
    private static final Pattern CHAR_PREFIX = Pattern.compile("0'");

    private static final PrologExpressionVisitor INSTANCE = new PrologExpressionVisitor();

    private PrologExpressionVisitor() {
    }

    public static PrologExpressionVisitor getInstance() {
        return INSTANCE;
    }

    public static <T extends ParserRuleContext> Function<T, Term> asFunction() {
        return it -> it.accept(PrologExpressionVisitor.getInstance());
    }

    @Override
    public Term visitClause(ClauseContext ctx) {
        return ctx.expression().accept(this);
    }

    @Override
    public Term visitSingletonTerm(SingletonTermContext ctx) {
        return visitTerm(ctx.term());
    }

    @Override
    public Term visitSingletonExpression(PrologParser.SingletonExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Term visitTerm(TermContext ctx) {
        if (ctx.isExpr) {
            return visitExpression(ctx.expression());
        } else {
            return ctx.children.get(0).accept(this);
        }
    }

    @Override
    public Term visitNumber(NumberContext ctx) {
        return super.visitNumber(ctx);
    }

    @Override
    public Term visitSet(SetContext ctx) {
        if (ctx.length > 0) {
            return new Struct("{}", ctx.items.stream().map(it -> it.accept(this)).toArray(Term[]::new));
        } else {
            return new Struct("{}");
        }
    }

    @Override
    public Term visitVariable(VariableContext ctx) {
        return new Var(ctx.value.getText());
    }

    @Override
    public Term visitExpression(ExpressionContext ctx) {
        Term result;

        if (ctx.isTerm) {
            result = this.visitTerm(ctx.left);
        } else if (INFIX.contains(ctx.associativity)) {
            result = visitInfixExpression(ctx);
        } else if (POSTFIX.contains(ctx.associativity)) {
            result = visitPostfixExpression(ctx);
        } else if (PREFIX.contains(ctx.associativity)) {
            result = visitPrefixExpression(ctx);
        } else if (ctx.exception != null) {
            throw ctx.exception;
        } else {
            throw new IllegalArgumentException();
        }

        final java.util.List<OuterContext> outers = flatten(ctx.outers.stream()).collect(Collectors.toList());

        return handleOuters(result, outers.stream());
    }

    private Stream<OuterContext> flatten(Stream<OuterContext> outers) {
        return outers.flatMap(o -> Stream.concat(Stream.of(o), flatten(o.outers.stream())));
    }

    private Term handleOuters(Term expression, Stream<OuterContext> outers) {
        Term result = expression;

        for (Iterator<OuterContext> i = outers.iterator(); i.hasNext();) {
            final OuterContext outer = i.next();

            final Stream<Term> operands = Stream.concat(
                    Stream.of(result),
                    outer.right.stream().map(it -> it.accept(this))
            );

            final Stream<String> operators = outer.operators.stream().map(op -> op.symbol.getText());

            switch (outer.associativity) {
                case XFY:
                    result = infixRight(operands, operators);
                    break;
                case YFX:
                    result = infixLeft(operands, operators);
                    break;
                case XFX:
                    result = infixNonAssociative(operands, operators);
                    break;
                case XF:
                case YF:
                    result = postfix(result, operators);
                    break;
                default:
                    throw new IllegalStateException();

            }
        }

        return result;
    }

    private Term visitPostfixExpression(ExpressionContext ctx) {
        return postfix(ctx.left.accept(this), ctx.operators.stream().map(it -> it.symbol.getText()));
    }

    private Term postfix(Term term, Stream<String> ops) {
        final Iterator<String> operator = ops.iterator();

        Term result = new Struct(operator.next(), term);
        while (operator.hasNext()) {
            result = new Struct(operator.next(), result);
        }
        return result;
    }

    private Term visitInfixExpression(ExpressionContext ctx) {
        if (ctx.associativity == XFY) {
            return visitInfixRightAssociativeExpression(ctx);
        } else if (ctx.associativity == YFX) {
            return visitInfixLeftAssociativeExpression(ctx);
        } else if (ctx.associativity == XFX){
            return visitInfixNonAssociativeExpression(ctx);
        } else {
            throw new IllegalStateException();
        }
    }

    private Term visitInfixNonAssociativeExpression(ExpressionContext ctx) {
        final Stream<Term> operands = Stream.<RuleContext>concat(Stream.of(ctx.left), ctx.right.stream())
                .map(it -> it.accept(this));

        final Stream<String> operators = ctx.operators.stream()
                .map(op -> op.symbol.getText());

        return infixNonAssociative(operands, operators);
    }

    private Term infixNonAssociative(Stream<Term> terms, Stream<String> ops) {
        final List<Term> operands = terms.collect(Collectors.toList());
        final List<String> operators = ops.collect(Collectors.toList());

        return new Struct(operators.get(0), operands.get(0), operands.get(1));
    }

    private Term visitPrefixExpression(PrologParser.ExpressionContext ctx) {
        return prefix(ctx.right.get(0).accept(this), ctx.operators.stream().map(it -> it.symbol.getText()));
    }

    private Term prefix(Term term, Stream<String> ops) {
        final List<String> operators = ops.collect(Collectors.toList());

        int i = operators.size() - 1;
        Term result = new Struct(operators.get(i--), term);
        for (; i >= 0; i--) {
            result = new Struct(operators.get(i), result);
        }
        return result;
    }

    private Stream<Term> streamOfOperands(PrologParser.ExpressionContext ctx) {
        return Stream.<RuleContext>concat(Stream.of(ctx.left), ctx.right.stream()).map(it -> it.accept(this));
    }

    private Stream<String> streamOfOperators(PrologParser.ExpressionContext ctx) {
        return ctx.operators.stream().map(op -> op.symbol.getText());
    }

    private Term visitInfixLeftAssociativeExpression(PrologParser.ExpressionContext ctx) {
        return infixLeft(streamOfOperands(ctx), streamOfOperators(ctx));
    }

    private Term infixLeft(Stream<Term> terms, Stream<String> ops) {
        final List<Term> operands = terms.collect(Collectors.toList());
        final List<String> operators = ops.collect(Collectors.toList());

        int i = 0;
        int j = 0;
        Term result = new Struct(operators.get(j++), operands.get(i++), operands.get(i++));
        for (; i < operands.size(); i++) {
            result = new Struct(operators.get(j++), result, operands.get(i));
        }
        return result;
    }

    private Term visitInfixRightAssociativeExpression(PrologParser.ExpressionContext ctx) {
        return infixRight(streamOfOperands(ctx), streamOfOperators(ctx));
    }

    private Term infixRight(Stream<Term> terms, Stream<String> ops) {
        final List<Term> operands = terms.collect(Collectors.toList());
        final List<String> operators = ops.collect(Collectors.toList());

        int i = operands.size() - 1;
        int j = operators.size() - 1;
        Term result = new Struct(operators.get(j--), operands.get(i - 1), operands.get(i));
        for (i -= 2; i >= 0; i--) {
            result = new Struct(operators.get(j--), operands.get(i), result);
        }
        return result;
    }

    private java.lang.Number parseInteger(IntegerContext ctx) {
        final String str = ctx.value.getText();
        int base;
        final String clean;

        if (ctx.isBin) {
            base = 2;
            clean = BIN_PREFIX.matcher(str).replaceAll("");
        } else if (ctx.isOct) {
            base = 8;
            clean = OCT_PREFIX.matcher(str).replaceAll("");
        } else if (ctx.isHex) {
            base = 16;
            clean = HEX_PREFIX.matcher(str).replaceAll("");
        } else if (ctx.isChar) {
            clean = CHAR_PREFIX.matcher(str).replaceAll("");
            return (int) clean.charAt(0);
        } else {
            base = 10;
            clean = str;
        }

        try {
            return Integer.parseInt(clean, base);
        } catch (NumberFormatException mayBeLong) {
            try {
                return Long.parseLong(clean, base);
            } catch (NumberFormatException notEvenLong) {
                throw new ParsingException(ctx.value, notEvenLong);
            }
        }
    }

    @Override
    public Term visitInteger(IntegerContext ctx) {
        java.lang.Number value = parseInteger(ctx);

        if (value instanceof Integer) {
            return new Int(value.intValue());
        } else {
            return new alice.tuprolog.Long(value.longValue());
        }
    }

    @Override
    public Term visitReal(PrologParser.RealContext ctx) {
        try {
            return new alice.tuprolog.Double(Double.parseDouble(ctx.value.getText()));
        } catch (NumberFormatException notAFloating) {
            throw new ParsingException(ctx.value, notAFloating);
        }
    }

    @Override
    public Term visitStructure(PrologParser.StructureContext ctx) {
        if (ctx.isList) {
            return new Struct();
        } else if (ctx.arity == 0) {
            return new Struct(ctx.functor.getText());
        } else {
            return new Struct(ctx.functor.getText(), ctx.args.stream().map(this::visitExpression).toArray(Term[]::new));
        }
    }

    @Override
    public Term visitList(PrologParser.ListContext ctx) {
        Stream<Term> terms = ctx.items.stream().map(this::visitExpression);
        if (ctx.hasTail) {
            terms = Stream.concat(terms, Stream.of(this.visitExpression(ctx.tail)));
        } else {
            return new Struct(terms.toArray(Term[]::new));
        }
        final List<Term> termsList = terms.collect(Collectors.toList());
        int i = termsList.size() - 1;
        Term result = new Struct(termsList.get(i - 1), termsList.get(i));
        for (i -= 2; i >= 0; i--) {
            result = new Struct(termsList.get(i), result);
        }
        return result;
    }


}
