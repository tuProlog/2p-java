:- op(1200, xfx, '-->').
:- op(200, xfx, '\\').

dcg_nonterminal(X) :- list(X), !, fail.
dcg_nonterminal(_).

dcg_terminals(Xs) :- list(Xs).

phrase(C,L) :- phrase_guard(C,L), phrase0(C,L).
phrase(C,L,R) :- phrase_guard(C,L,R), phrase0(C,L,R).

phrase0(Category, String, Left) :- dcg_parse(Category, String \ Left).
phrase0(Category, [H | T]) :- dcg_parse(Category, [H | T] \ []).
phrase0(Category,[]) :- dcg_parse(Category, [] \ []).

dcg_parse(A, Tokens) :- dcg_nonterminal(A), (A --> B), dcg_parse(B, Tokens).
dcg_parse((A, B), Tokens \ Xs) :- dcg_parse(A, Tokens \ Tokens1), dcg_parse(B, Tokens1 \ Xs).
dcg_parse(A, Tokens) :- dcg_terminals(A), dcg_connect(A, Tokens).
dcg_parse({A}, Xs \ Xs) :- call(A).

dcg_connect([], Xs \ Xs).
dcg_connect([W | Ws], [W | Xs] \ Ys) :- dcg_connect(Ws, Xs \ Ys).
