/*
 * tuProlog - Copyright (C) 2001-2007 aliCE team at deis.unibo.it
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

package alice.util;

import alice.tuprolog.Struct;
import alice.tuprolog.Term;

import java.util.NoSuchElementException;
import java.util.Objects;

public class DeepLogicListIterator implements java.util.Iterator<Term> {

    private Term list;

    public DeepLogicListIterator(Struct term) {
        this.list = Objects.requireNonNull(term);
        if (!term.isCons()) {
            throw new IllegalArgumentException(String.format("The structure %s is not a list.", term));
        }
    }

    @Override
    public boolean hasNext() {
        return list != null;
    }

    @Override
    public Term next() {
        if (list.isList()) {
            final Struct list = this.list.castTo(Struct.class);
            final Term head = list.getTerm(0);
            this.list = list.getTerm(1);
            return head;
        } else {
            final Term last = list;
            list = null;
            return last;
        }
    }
}