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

import alice.tuprolog.Library;
import alice.tuprolog.PrologError;
import alice.tuprolog.Term;
import alice.tuprolog.Var;
import alice.util.Tools;

import java.io.IOException;

/**
 * Library for managing DCGs.
 * <p>
 * Library/Theory dependency: BasicLibrary
 */
public class EDCGLibrary extends Library {
    private static final long serialVersionUID = 1L;

    public EDCGLibrary() {
    }

    private static final String THEORY;

    static {
        try {
            THEORY = Tools.loadText(EDCGLibrary.class.getResourceAsStream(EDCGLibrary.class.getSimpleName() + ".pl"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getTheory() {
        return THEORY;
    }

    public boolean phrase_guard_2(Term arg0, Term arg1) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(engine.getEngineManager(), 1);
        }
        return true;
    }

    public boolean phrase_guard_3(Term arg0, Term arg1, Term arg2) throws PrologError {
        arg0 = arg0.getTerm();
        if (arg0 instanceof Var) {
            throw PrologError.instantiation_error(engine.getEngineManager(), 1);
        }
        return true;
    }

}