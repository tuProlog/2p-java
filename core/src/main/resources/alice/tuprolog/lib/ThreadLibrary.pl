thread_execute(ID, GOAL) :-
    thread_create(ID, GOAL),
    '$next'(ID).

'$next'(ID).
'$next'(ID) :- '$thread_execute2'(ID).

'$thread_execute2'(ID) :- not thread_has_next(ID),!,false.
'$thread_execute2'(ID) :- thread_next_sol(ID).
'$thread_execute2'(ID) :- '$thread_execute2'(ID).

with_mutex(MUTEX, GOAL) :- mutex_lock(MUTEX), call(GOAL), !, mutex_unlock(MUTEX).
with_mutex(MUTEX, GOAL) :- mutex_unlock(MUTEX), fail.