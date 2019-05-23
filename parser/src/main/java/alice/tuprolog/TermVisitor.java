package alice.tuprolog;

import org.antlr.v4.runtime.RuleContext;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static alice.tuprolog.dynamic.Associativity.*;

public class TermVisitor extends PrologParserBaseVisitor<Term> {

    private static final TermVisitor INSTANCE = new TermVisitor();

    public static TermVisitor get() {
        return INSTANCE;
    }

    @Override
    public Term visitSingletonTerm(PrologParser.SingletonTermContext ctx) {
        return visitTerm(ctx.term());
    }

    @Override
    public Term visitSingletonExpression(PrologParser.SingletonExpressionContext ctx) {
        return visitExpression(ctx.expression());
    }

    @Override
    public Term visitTerm(PrologParser.TermContext ctx) {
        if (ctx.isExpr) {
            return visitExpression(ctx.expression());
        } else {
            return ctx.children.get(0).accept(this);
        }
    }

    @Override
    public Term visitNumber(PrologParser.NumberContext ctx) {
        return super.visitNumber(ctx);
    }

    @Override
    public Term visitVariable(PrologParser.VariableContext ctx) {
        return Var.of(ctx.value.getText());
    }

    @Override
    public Term visitExpression(PrologParser.ExpressionContext ctx) {
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

        final java.util.List<PrologParser.OuterContext> outers = flatten(ctx.outers.stream()).collect(Collectors.toList());

        return handleOuters(result, outers.stream());
    }

    private Stream<PrologParser.OuterContext> flatten(Stream<PrologParser.OuterContext> outers) {
        return outers.flatMap(o -> Stream.concat(Stream.of(o), flatten(o.outers.stream())));
    }

    private Term handleOuters(Term expression, Stream<PrologParser.OuterContext> outers) {
        Term result = expression;

        for (Iterator<PrologParser.OuterContext> i = outers.iterator(); i.hasNext(); ) {
            final PrologParser.OuterContext outer = i.next();

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
                    result = infixRight(operands, operators);
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

    private Term visitPostfixExpression(PrologParser.ExpressionContext ctx) {
        return postfix(ctx.left.accept(this), ctx.operators.stream().map(it -> it.symbol.getText()));
    }

    private Term postfix(Term term, Stream<String> ops) {
        final Iterator<String> operator = ops.iterator();

        Term result = Struct.of(operator.next(), term);
        while (operator.hasNext()) {
            result = Struct.of(operator.next(), result);
        }
        return result;
    }

    private Term visitInfixExpression(PrologParser.ExpressionContext ctx) {


        if (ctx.associativity == XFY) {
            return visitInfixRightAssociativeExpression(ctx);
        } else if (ctx.associativity == YFX) {
            return visitInfixLeftAssociativeExpression(ctx);
        } else if (ctx.associativity == XFX) {
            return visitInfixNonAssociativeExpression(ctx);
        } else {
            throw new IllegalStateException();
        }
    }

    private Term visitInfixNonAssociativeExpression(PrologParser.ExpressionContext ctx) {
        final Stream<Term> operands = Stream.<RuleContext>concat(Stream.of(ctx.left), ctx.right.stream())
                .map(it -> it.accept(this));

        final Stream<String> operators = ctx.operators.stream()
                                                      .map(op -> op.symbol.getText());

        return infixNonAssociative(operands, operators);
    }

    private Term infixNonAssociative(Stream<Term> terms, Stream<String> ops) {
        final java.util.List<Term> operands = terms.collect(Collectors.toList());
        final java.util.List<String> operators = ops.collect(Collectors.toList());

        return Struct.of(operators.get(0), operands.get(0), operands.get(1));
    }

    private Term visitPrefixExpression(PrologParser.ExpressionContext ctx) {
        return prefix(ctx.right.get(0).accept(this), ctx.operators.stream().map(it -> it.symbol.getText()));
    }

    private Term prefix(Term term, Stream<String> ops) {
        final java.util.List<String> operators = ops.collect(Collectors.toList());

        int i = operators.size() - 1;
        Term result = Struct.of(operators.get(i--), term);
        for (; i >= 0; i--) {
            result = Struct.of(operators.get(i), result);
        }
        return result;
    }


    private Term visitInfixLeftAssociativeExpression(PrologParser.ExpressionContext ctx) {
        final Stream<Term> operands = Stream.<RuleContext>concat(Stream.of(ctx.left), ctx.right.stream())
                .map(it -> it.accept(this));

        final Stream<String> operators = ctx.operators.stream()
                                                      .map(op -> op.symbol.getText());

        return infixLeft(operands, operators);
    }

    private Term infixLeft(Stream<Term> terms, Stream<String> ops) {
        final java.util.List<Term> operands = terms.collect(Collectors.toList());
        final java.util.List<String> operators = ops.collect(Collectors.toList());

        int i = 0;
        int j = 0;
        Term result = Struct.of(operators.get(j++), operands.get(i++), operands.get(i++));
        for (; i < operands.size(); i++) {
            result = Struct.of(operators.get(j++), result, operands.get(i));
        }
        return result;
    }

    private Term visitInfixRightAssociativeExpression(PrologParser.ExpressionContext ctx) {
        final Stream<Term> operands = Stream.<RuleContext>concat(Stream.of(ctx.left), ctx.right.stream())
                .map(it -> it.accept(this));

        final Stream<String> operators = ctx.operators.stream()
                                                      .map(op -> op.symbol.getText());

        return infixRight(operands, operators);
    }

    private Term infixRight(Stream<Term> terms, Stream<String> ops) {
        final java.util.List<Term> operands = terms.collect(Collectors.toList());
        final java.util.List<String> operators = ops.collect(Collectors.toList());

        int i = operands.size() - 1;
        int j = operators.size() - 1;
        Term result = Struct.of(operators.get(j--), operands.get(i - 1), operands.get(i));
        for (i -= 2; i >= 0; i--) {
            result = Struct.of(operators.get(j--), operands.get(i), result);
        }
        return result;
    }

    @Override
    public Term visitInteger(PrologParser.IntegerContext ctx) {
        return Int.of(ctx.value.getText());
    }

    @Override
    public Term visitReal(PrologParser.RealContext ctx) {
        return Real.of(ctx.value.getText());
    }

    @Override
    public Term visitStructure(PrologParser.StructureContext ctx) {
        if (ctx.isList) {
            return Empty.get();
        } else if (ctx.arity == 0) {
            return Atom.of(ctx.functor.getText());
        } else {
            return Struct.of(ctx.functor.getText(), ctx.args.stream().map(this::visitExpression));
        }
    }

    @Override
    public Term visitList(PrologParser.ListContext ctx) {
        Stream<Term> terms = ctx.items.stream().map(this::visitExpression);
        if (ctx.hasTail) {
            terms = Stream.concat(terms, Stream.of(this.visitExpression(ctx.tail)));
        } else {
            terms = Stream.concat(terms, Stream.of(Empty.get()));
        }
        return List.of(terms);
    }
}
