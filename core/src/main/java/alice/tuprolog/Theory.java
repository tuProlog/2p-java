/*
 * tuProlog - Copyright (C) 2001-2007  aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.json.JSONSerializerManager;
import alice.tuprolog.parser.ParsingException;
import alice.tuprolog.parser.PrologExpressionVisitor;
import alice.tuprolog.parser.PrologParserFactory;
import alice.tuprolog.parser.dynamic.Associativity;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents prolog theory which can be provided
 * to a prolog engine.
 * <p>
 * Actually theory incapsulates only textual representation
 * of prolog theories, without doing any check about validity
 *
 * @see Prolog
 */
public class Theory implements Serializable {

    public static Theory empty() {
        return new Theory().setOperatorManager(OperatorManager.empty());
    }

    public static Theory parseLazily(String source) {
        return new Theory(source).setOperatorManager(OperatorManager.empty());
    }

    public static Theory parse(String source) {
        Theory t = new Theory(source).setOperatorManager(OperatorManager.empty());
        t.getClauses();
        return t;
    }

    public static Theory parseLazily(InputStream source) throws IOException {
        return new Theory(source).setOperatorManager(OperatorManager.empty());
    }

    public static Theory parse(InputStream source) throws IOException {
        Theory t = new Theory(source).setOperatorManager(OperatorManager.empty());
        t.getClauses();
        return t;
    }

    public static Theory emptyWithStandardOperators() {
        return new Theory().setOperatorManager(OperatorManager.standardOperators());
    }

    public static Theory parseLazilyWithStandardOperators(String source) {
        return new Theory(source).setOperatorManager(OperatorManager.standardOperators());
    }

    public static Theory parseWithStandardOperators(String source) {
        Theory t = new Theory(source).setOperatorManager(OperatorManager.standardOperators());
        t.getClauses();
        return t;
    }

    public static Theory parseLazilyWithStandardOperators(InputStream source) throws IOException {
        return new Theory(source).setOperatorManager(OperatorManager.standardOperators());
    }

    public static Theory parseWithStandardOperators(InputStream source) throws IOException {
        Theory t = new Theory(source).setOperatorManager(OperatorManager.standardOperators());
        t.getClauses();
        return t;
    }

    public static Theory emptyWithOperators(OperatorManager operatorManager) {
        return new Theory().setOperatorManager(operatorManager);
    }

    public static Theory parseLazilyWithOperators(String source, OperatorManager operatorManager) {
        return new Theory(source).setOperatorManager(operatorManager);
    }

    public static Theory parseWithOperators(String source, OperatorManager operatorManager) {
        Theory t = new Theory(source).setOperatorManager(operatorManager);
        t.getClauses();
        return t;
    }

    public static Theory parseLazilyWithOperators(InputStream source, OperatorManager operatorManager) throws IOException {
        return new Theory(source).setOperatorManager(operatorManager);
    }

    public static Theory parseWithOperators(InputStream source, OperatorManager operatorManager) throws IOException {
        Theory t = new Theory(source).setOperatorManager(operatorManager);
        t.getClauses();
        return t;
    }

    public static Theory fromPrologList(Struct clauses) {
        return new Theory(clauses);
    }

    public static Theory of(Term... terms) {
        return new Theory(Arrays.asList(terms));
    }

    public static Theory of(Collection<? extends Term> terms) {
        return new Theory(terms);
    }

    private String theory;
    private Struct clauseList;
    private List<Term> clauses;
    private OperatorManager operatorManager = OperatorManager.standardOperators();

    /**
     * Creates a theory getting its source text from an input stream
     *
     * @param is the input stream acting as source
     */
    @Deprecated
    public Theory(InputStream is) throws IOException {
        byte[] info = new byte[is.available()];
        is.read(info);
        theory = new String(info);
    }

    /**
     * Creates a theory from its source text
     *
     * @param theory the source text
     * @throws InvalidTheoryException if theory is null
     */
    @Deprecated
    public Theory(String theory) {
        if (theory == null) {
            throw new InvalidTheoryException();
        }
        this.theory = theory;
    }

    @Deprecated
    Theory() {
        this("", Collections.emptyList());
    }

    /**
     * Creates a theory from a clause list
     *
     * @param clauseList the source text
     * @throws InvalidTheoryException if clauseList is null or is not a prolog list
     */
    @Deprecated
    public Theory(Struct clauseList) {
        if (clauseList == null || !clauseList.isList()) {
            throw new InvalidTheoryException();
        }
        this.clauseList = clauseList;
        getClauses();
        synchroniseOperators();
    }

    private Theory(Collection<? extends Term> clauses) {
        if (clauses == null) {
            throw new InvalidTheoryException();
        }
        this.clauses = clauses.stream().peek(it -> {
            if (!(it instanceof Struct)) {
                throw new InvalidTheoryException();
            }
        }).collect(Collectors.toList());

        synchroniseOperators();
    }

    private Theory(String text, Collection<? extends Term> clauses) {
        this(Objects.requireNonNull(clauses));
        this.theory = Objects.requireNonNull(text);
    }

    //Alberto
    public static Theory fromJSON(String jsonString) {
        return JSONSerializerManager.fromJSON(jsonString, Theory.class);
    }

    private List<Term> slitPrologList() {
        final List<Term> clauses = new LinkedList<>();
        for (Iterator<? extends Term> i = clauseList.listIterator(); i.hasNext();) {
            final Term it = i.next();
            if (!(it instanceof Struct)) {
                throw new InvalidTheoryException();
            }
            clauses.add(it);
        }
        return clauses;
    }

    private List<Term> parseText() {
        try {
            return PrologParserFactory.getInstance()
                    .parseClauses(theory, operatorManager)
                    .map(PrologExpressionVisitor.asFunction())
                    .collect(Collectors.toList());
        } catch (ParsingException e) {
            throw e.toInvalidTheoryException();
        }
    }

    public List<Term> getClauses() {
        if (clauses == null) {
            if (clauseList != null) {
                clauses = slitPrologList();
            } else if (theory != null) {
                clauses = parseText();
            } else {
                throw new IllegalStateException();
            }
        }

        return clauses;
    }

    private void synchroniseOperators() {
        getClauses().stream()
                    .filter(Struct.class::isInstance)
                    .map(Struct.class::cast)
                    .filter(it -> it.getArity() == 1 && ":-".equals(it.getName()))
                    .map(it -> it.getArg(0))
                    .filter(Struct.class::isInstance)
                    .map(Struct.class::cast)
                    .filter(it -> it.getArity() == 3 && "op".equals(it.getName())
                                  && it.getArg(0) instanceof Number
                                  && it.getArg(1).getTerm() instanceof Struct
                                  && it.getArg(2).getTerm() instanceof Struct)
                    .forEach(op -> {
                        final int priority = ((Number)op.getArg(0)).intValue();
                        final Struct type = (Struct) op.getArg(1).getTerm();
                        final Struct name = (Struct) op.getArg(2).getTerm();

                        if (type.getArity() == 0 && name.getArity() == 0 && Associativity.isAssociativity(type.getName())) {
                            operatorManager.add(name.getName(), type.getName(), priority);
                        }
                    });
    }

    public String getText() {
        if (theory == null) {
            theory = clauses.stream().map(Term::toString).collect(Collectors.joining(".\n", "", ".\n"));
        }

        return theory;
    }

    private void setText(String text) {
        this.theory = text;
    }

    public Iterator<? extends Term> iterator(Prolog engine) {
        return getClauses().iterator();
    }

    /**
     * Adds (appends) a theory to this.
     *
     * @param other is the theory to be appended
     * @throws InvalidTheoryException if the theory object are not compatibles (they are
     *           compatibles when both have been built from texts or both from clause lists)
     */
    public void append(Theory other) {
        setText(getText() + other.getText());
        getClauses().addAll(other.getClauses());
        synchroniseOperators();
    }

    /**
     * Checks if the theory has been built
     * from a text or a clause list
     */
    boolean isTextual() {
        return theory != null;
    }

    boolean isParsed() {
        return clauses != null;
    }

    public OperatorManager getOperatorManager() {
        return operatorManager;
    }

    private Theory setOperatorManager(final OperatorManager operatorManager) {
        this.operatorManager = Objects.requireNonNull(operatorManager);
        return this;
    }

    Struct getClauseListRepresentation() {
        if (clauseList == null) {
            clauseList = new Struct(getClauses());
        }
        return clauseList;
    }

    public String toString() {
        return getText();
    }

    //Alberto
    public String toJSON() {
        return JSONSerializerManager.toJSON(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Theory theory = (Theory) o;
        return Objects.equals(getClauses(), theory.getClauses());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClauses());
    }
}