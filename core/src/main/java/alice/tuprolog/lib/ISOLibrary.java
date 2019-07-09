/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tuprolog.lib;

import alice.tuprolog.Number;
import alice.tuprolog.*;
import alice.util.Tools;

import java.io.IOException;
import java.lang.Double;
import java.lang.Long;
import java.util.Iterator;

import static alice.util.Tools.removeApices;
import static alice.util.Tools.unescape;

/**
 * This class represents a tuProlog library providing most of the built-ins
 * predicates and functors defined by ISO standard.
 * <p>
 * Library/Theory dependency: BasicLibrary
 */
public class ISOLibrary extends Library {
    private static final long serialVersionUID = 1L;

    public ISOLibrary() {
    }

    public boolean atom_length_2(Term arg0, Term len) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(getEngine().getEngineManager(), 1);
        }
        if (!arg0.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
        }
        if (len instanceof Var && ((Var) len).isBound()) {
            len = len.getTerm();
        }
        if (len instanceof Var || len instanceof Int || len instanceof alice.tuprolog.Long) {
            Struct atom = (Struct) arg0;
            return unify(len, Int.of(atom.getName().length()));
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
        }
    }

    public boolean number_chars_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            if (!arg1.isList()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
            }
            Struct list = (Struct) arg1;
            if (list.isEmptyList()) {
                throw PrologError.domain_error(getEngine().getEngineManager(), 2, "[Char | Chars]", arg1);
            }
            StringBuilder sb = new StringBuilder();

            for (Iterator<? extends Term> i = list.listIterator(); i.hasNext(); ) {
                Term charTerm = i.next();
                if (charTerm instanceof Struct) {
                    Struct charAtom = (Struct) charTerm;
                    if (charAtom.isAtom()) {
                        String rawCharString = charAtom.getName();
                        if (rawCharString.equals("''")) {
                            sb.append("'");
                            continue;
                        }

                        String charString = unescape(removeApices(rawCharString));
                        if (charString.length() == 1) {
                            sb.append(charString);
                            continue;
                        }
                    }
                }
                throw PrologError.domain_error(getEngine().getEngineManager(), 2, "[char | Chars]", arg1);
            }

            String numString = sb.toString().trim();

            if (numString.contains("'")) {
                numString = numString.replace("0'", "");
                return unify(arg0, Int.of(numString.charAt(0)));
            }

            try {

                if (numString.contains(".")) {
                    double val = Double.parseDouble(numString);
                    return unify(arg0, alice.tuprolog.Double.of(val));
                }

                int radix = 10;

                if (numString.contains("x") || numString.contains("X")) {
                    radix = 16;
                    numString = numString.replace("0x", "").replace("0X", "");
                } else if (numString.contains("o") || numString.contains("O")) {
                    radix = 8;
                    numString = numString.replace("0o", "").replace("0O", "");
                } else if (numString.contains("b") || numString.contains("B")) {
                    radix = 2;
                    numString = numString.replace("0b", "").replace("0B", "");
                }

                long val = Long.parseLong(numString, radix);
                return unify(arg0, Int.of(val));
            } catch (NumberFormatException e) {
                throw PrologError.domain_error(getEngine().getEngineManager(), 2, "[digit | Digits]", arg1);
            }

        } else {
            if (!(arg0 instanceof Number)) {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "number", arg0);
            }
            String string = arg0.toString();
            Term[] numberList = new Term[string.length()];
            for (int i = 0; i < string.length(); i++) {
                numberList[i] = Struct.atom(new String(new char[]{string.charAt(i)}));
            }
            Struct list = Struct.list(numberList);

            return unify(arg1, list);
        }
    }

    public boolean  atom_chars_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            if (!arg1.isList()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
            }
            Struct list = (Struct) arg1;
            if (list.isEmptyList()) {
                return unify(arg0, Struct.of(""));
            }
            String st = "";
            while (!(list.isEmptyList())) {
                String st1 = list.getTerm(0).toString();
//                try {
                if (st1.startsWith("'") && st1.endsWith("'")) {
                    st1 = st1.substring(1, st1.length() - 1);
                }
                    /*else
                    {
                    	byte[] b= st1.getBytes();
                    	st1=""+b[0];
                    }*/

//                } catch (Exception ex) {
//                }
                st = st.concat(st1);
                list = (Struct) list.getTerm(1);
            }
            return unify(arg0, Struct.of(st));
        } else {
            if (!arg0.isAtom()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
            }
            String st = ((Struct) arg0).getName();
            Term[] tlist = new Term[st.length()];
            for (int i = 0; i < st.length(); i++) {
                tlist[i] = Struct.of(new String(new char[]{st.charAt(i)}));
            }
            Struct list = Struct.list(tlist);
            /*
             * for (int i=0; i<st.length(); i++){ Struct ch=Struct.of(new
             * String(new char[]{ st.charAt(st.length()-i-1)} )); list=new
             * Struct( ch, list); }
             */

            return unify(arg1, list);
        }
    }

    public boolean atom_codes_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg0 instanceof Var) {
            if (!arg1.isList()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 2, "list", arg1);
            }
            Struct list = (Struct) arg1;
            if (list.isEmptyList()) {
                return unify(arg0, Struct.of(""));
            }
            StringBuilder sb = new StringBuilder();
            while (!(list.isEmptyList())) {
                Term code = list.listHead();

                if (code instanceof Var && ((Var) code).isBound()) {
                    code = code.getTerm();
                }

                if (code instanceof alice.tuprolog.Number) {
                    sb.append((char) ((Number) code).intValue());
                }

                list = list.listTail();
            }
            return unify(arg0, Struct.of(sb.toString()));
        } else {
            if (!arg0.isAtom()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
            }
            String st = ((Struct) arg0).getName();
            Term[] codesList = new Term[st.length()];
            for (int i = 0; i < st.length(); i++) {
                codesList[i] = Int.of(st.charAt(i));
            }
            Struct list = Struct.list(codesList);
            return unify(arg1, list);
        }
    }

    public boolean char_code_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        arg1 = arg1.getTerm();
        if (arg1 instanceof Var) {
            if (arg0.isAtom()) {
                String st = ((Struct) arg0).getName();
                if (st.length() <= 1) {
                    return unify(arg1, Int.of(st.charAt(0)));
                } else {
                    throw PrologError.type_error(getEngine().getEngineManager(), 1,
                            "character", arg0);
                }
            } else {
                throw PrologError.type_error(getEngine().getEngineManager(), 1,
                        "character", arg0);
            }
        } else if ((arg1 instanceof Int)
                || (arg1 instanceof alice.tuprolog.Long)) {
            char c = (char) ((Number) arg1).intValue();
            return unify(arg0, Struct.of("" + c));
        } else {
            throw PrologError.type_error(getEngine().getEngineManager(), 2,
                    "integer", arg1);
        }
    }

    //

    // functors

    public Term sin_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.sin(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term cos_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.cos(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term exp_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.exp(((Number) val0).doubleValue()));
        }
        return null;
    }

    public Term atan_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.atan(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term log_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.log(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term sqrt_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(Math.sqrt(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term abs_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long) {
            return Int.of(Math.abs(((Number) val0).intValue()));
        }
        if (val0 instanceof alice.tuprolog.Double
                || val0 instanceof alice.tuprolog.Float) {
            return alice.tuprolog.Double.of(Math.abs(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term sign_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long) {
            return alice.tuprolog.Double.of(
                    ((Number) val0).intValue() > 0 ? 1.0 : -1.0);
        }
        if (val0 instanceof alice.tuprolog.Double
                || val0 instanceof alice.tuprolog.Float) {
            return alice.tuprolog.Double.of(
                    ((Number) val0).doubleValue() > 0 ? 1.0 : -1.0);
        }
        return null;
    }

    public Term float_integer_part_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of((long) Math.rint(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term float_fractional_part_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            double fl = ((Number) val0).doubleValue();
            return alice.tuprolog.Double.of(Math.abs(fl - Math.rint(fl)));
        }
        return null;
    }

    public Term float_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Double.of(((Number) val0).doubleValue());
        }
        return null;
    }

    public Term floor_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return Int.of((int) Math.floor(((Number) val0).doubleValue()));
        }
        return null;
    }

    public Term round_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return alice.tuprolog.Long.of(Math.round(((Number) val0)
                    .doubleValue()));
        }
        return null;
    }

    public Term truncate_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        }  catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return Int.of((int) Math.rint(((Number) val0).doubleValue()));
        }
        return null;
    }

    public Term ceiling_1(Term val) {
        Term val0 = null;
        try {
            val0 = evalExpression(val);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number) {
            return Int.of((int) Math.ceil(((Number) val0).doubleValue()));
        }
        return null;
    }

    public Term div_2(Term v0, Term v1) throws PrologError {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(v0);
            val1 = evalExpression(v1);
        }  catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number && val1 instanceof Number) {
            return getIntegerNumber(((Number) val0).intValue() / ((Number) val1).intValue());
        }
        return null;
    }

    public Term mod_2(Term v0, Term v1) throws PrologError {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(v0);
            val1 = evalExpression(v1);
        }  catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number && val1 instanceof Number) {
            int x = ((Number) val0).intValue();
            int y = ((Number) val1).intValue();
            int f = java.lang.Double.valueOf(Math.floor((double) x / (double) y)).intValue();
            return Int.of(x - (f * y));
        }
        return null;
    }

    public Term rem_2(Term v0, Term v1) {
        Term val0 = null;
        Term val1 = null;
        try {
            val0 = evalExpression(v0);
            val1 = evalExpression(v1);
        } catch (ArithmeticException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
        if (val0 instanceof Number && val1 instanceof Number) {
            return alice.tuprolog.Double.of(Math.IEEEremainder(((Number) val0)
                    .doubleValue(), ((Number) val1).doubleValue()));
        }
        return null;
    }

    private static final String THEORY;

    static {
        try {
            THEORY = Tools.loadText(ISOLibrary.class.getResourceAsStream(ISOLibrary.class.getSimpleName() + ".pl"))
                          .replace("%%MAX_INT%%", Long.toString(Long.MAX_VALUE))
                    .replace("%%MAX_ARITY%%", Long.toString(Integer.MAX_VALUE))
                          .replace("%%MIN_INT%%", Long.toString(Long.MIN_VALUE));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getTheory() {
        return THEORY;
    }

    public boolean sub_atom_guard_5(Term arg0, Term arg1, Term arg2, Term arg3, Term arg4) throws PrologError {
        arg0 = arg0.getTerm();
        if (!arg0.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "atom", arg0);
        }
        return true;
    }

}
