/**
 * @author Eleonora Cau
 */

package alice.tuprolog.lib;

import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidTermException;
import alice.tuprolog.exceptions.NoSolutionException;
import alice.util.Tools;

import java.io.IOException;


public class ThreadLibrary extends Library {

    //Tenta di unificare a t l'identificativo del thread corrente
    public boolean thread_id_1(Term t) throws PrologError {
        int id = getEngine().getEngineManager().runnerId();
        unify(t, Int.of(id));
        return true;
    }

    //Crea un nuovo thread di identificatore id che comincia ad eseguire il goal dato
    public boolean thread_create_2(Term id, Term goal) {
        return getEngine().getEngineManager().threadCreate(id, goal);
    }

    /*Aspetta la terminazione del thread di identificatore id e ne raccoglie il risultato,
    unificando il goal risolto a result. Il thread viene eliminato dal sistema*/
    public boolean thread_join_2(Term id, Term result) throws PrologError {
        id = id.getTerm();
        if (!(id instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "integer", id);
        }
        SolveInfo res = getEngine().getEngineManager().join(((Int) id).intValue());
        if (res == null) {
            return false;
        }
        Term status;
        try {
            status = res.getSolution();
        } catch (NoSolutionException e) {
            //status = new Struct("FALSE");
            return false;
        }
        try {
            unify(result, status);
        } catch (InvalidTermException e) {
            throw PrologError.syntax_error(getEngine().getEngineManager(), -1, e.getLine(), e.getPositionInLine(), result);
        }
        return true;
    }

    public boolean thread_read_2(Term id, Term result) throws PrologError {
        id = id.getTerm();
        if (!(id instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1, "integer", id);
        }
        SolveInfo res = getEngine().getEngineManager().read(((Int) id).intValue());
        if (res == null) {
            return false;
        }
        Term status;
        try {
            status = res.getSolution();
        } catch (NoSolutionException e) {
            //status = new Struct("FALSE");
            return false;
        }
        try {
            unify(result, status);
        } catch (InvalidTermException e) {
            throw PrologError.syntax_error(getEngine().getEngineManager(), -1, e.getLine(), e.getPositionInLine(), result);
        }
        return true;
    }

    public boolean thread_has_next_1(Term id) throws PrologError {
        id = id.getTerm();
        if (!(id instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "integer", id);
        }
        return getEngine().getEngineManager().hasNext(((Int) id).intValue());
    }


    public boolean thread_next_sol_1(Term id) throws PrologError {
        id = id.getTerm();
        if (!(id instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "integer", id);
        }
        return getEngine().getEngineManager().nextSolution(((Int) id).intValue());
    }

    public boolean thread_detach_1(Term id) throws PrologError {
        id = id.getTerm();
        if (!(id instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "integer", id);
        }
        getEngine().getEngineManager().detach(((Int) id).intValue());
        return true;
    }

    public boolean thread_sleep_1(Term millisecs) throws PrologError {
        millisecs = millisecs.getTerm();
        if (!(millisecs instanceof Int)) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "integer", millisecs);
        }
        long time = ((Int) millisecs).intValue();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("ERRORE SLEEP");
            return false;
        }
        return true;
    }

    public boolean thread_send_msg_2(Term id, Term msg) throws PrologError {
        id = id.getTerm();
        if (id instanceof Int) {
            return getEngine().getEngineManager().sendMsg(((Int) id).intValue(), msg);
        }
        if (!id.isAtomic() || !id.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom, atomic or integer", id);
        }
        return getEngine().getEngineManager().sendMsg(id.toString(), msg);
    }

    public boolean thread_get_msg_2(Term id, Term msg) throws PrologError {
        id = id.getTerm();
        if (id instanceof Int) {
            return getEngine().getEngineManager().getMsg(((Int) id).intValue(), msg);
        }
        if (!id.isAtom() || !id.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom, atomic or integer", id);
        }
        return getEngine().getEngineManager().getMsg(id.toString(), msg);
    }

    public boolean thread_peek_msg_2(Term id, Term msg) throws PrologError {
        id = id.getTerm();
        if (id instanceof Int) {
            return getEngine().getEngineManager().peekMsg(((Int) id).intValue(), msg);
        }
        if (!id.isAtom() || !id.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom, atomic or integer", id);
        }
        return getEngine().getEngineManager().peekMsg(id.toString(), msg);
    }

    public boolean thread_wait_msg_2(Term id, Term msg) throws PrologError {
        id = id.getTerm();
        if (id instanceof Int) {
            return getEngine().getEngineManager().waitMsg(((Int) id).intValue(), msg);
        }
        if (!id.isAtom() || !id.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom, atomic or integer", id);
        }
        return getEngine().getEngineManager().waitMsg(id.toString(), msg);
    }

    public boolean thread_remove_msg_2(Term id, Term msg) throws PrologError {
        id = id.getTerm();
        if (id instanceof Int) {
            return getEngine().getEngineManager().removeMsg(((Int) id).intValue(), msg);
        }
        if (!id.isAtom() || !id.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom, atomic or integer", id);
        }
        return getEngine().getEngineManager().removeMsg(id.toString(), msg);
    }

    public boolean msg_queue_create_1(Term q) throws PrologError {
        q = q.getTerm();
        if (!q.isAtomic() || !q.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", q);
        }
        return getEngine().getEngineManager().createQueue(q.toString());
    }

    public boolean msg_queue_destroy_1(Term q) throws PrologError {
        q = q.getTerm();
        if (!q.isAtomic() || !q.isAtom()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", q);
        }
        getEngine().getEngineManager().destroyQueue(q.toString());
        return true;
    }

    public boolean msg_queue_size_2(Term id, Term n) throws PrologError {
        id = id.getTerm();
        int size;
        if (id instanceof Int) {
            size = getEngine().getEngineManager().queueSize(((Int) id).intValue());
        } else {
            if (!id.isAtom() || !id.isAtomic()) {
                throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                             "atom, atomic or integer", id);
            }
            size = getEngine().getEngineManager().queueSize(id.toString());
        }
        if (size < 0) {
            return false;
        }
        return unify(n, Int.of(size));
    }

    public boolean mutex_create_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        return getEngine().getEngineManager().createLock(mutex.toString());
    }

    public boolean mutex_destroy_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        getEngine().getEngineManager().destroyLock(mutex.toString());
        return true;
    }

    public boolean mutex_lock_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        return getEngine().getEngineManager().mutexLock(mutex.toString());
    }

    public boolean mutex_trylock_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        return getEngine().getEngineManager().mutexTryLock(mutex.toString());
    }

    public boolean mutex_unlock_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        return getEngine().getEngineManager().mutexUnlock(mutex.toString());
    }

    public boolean mutex_isLocked_1(Term mutex) throws PrologError {
        mutex = mutex.getTerm();
        if (!mutex.isAtom() || !mutex.isAtomic()) {
            throw PrologError.type_error(getEngine().getEngineManager(), 1,
                                         "atom or atomic", mutex);
        }
        return getEngine().getEngineManager().isLocked(mutex.toString());
    }

    public boolean mutex_unlock_all_0() {
        getEngine().getEngineManager().unlockAll();
        return true;
    }

    private static final String THEORY;

    static {
        try {
            THEORY = Tools.loadText(ThreadLibrary.class.getResourceAsStream(ThreadLibrary.class.getSimpleName() + ".pl"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String getTheory() {
        return THEORY;
    }
}
