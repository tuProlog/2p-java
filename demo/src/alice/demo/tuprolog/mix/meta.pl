
solve((A,B)):-
    !,
    solve(A),
    solve(B).

solve(X):-
    write('solving '),write(X),nl,
    clause(X,Body),
    solve(Body).

solve(X):-
    not clause(X,_),
    X.


