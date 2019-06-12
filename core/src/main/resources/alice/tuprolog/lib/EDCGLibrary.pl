:- op(1200, xfx, '==>').
:- op(200, xfx, '\\').
:- op(200, xfx, ';').
:- op(200, fx, '*'). % Zero or more productions
:- op(200, fx, '+'). % One or more productions
:- op(200, fx, '?'). % Zero or one production
:- op(200, fx, '^'). % Exactly N productions (for parsing only)
:- op(200, fx, '#'). % Exactly N productions (for AST generation)

edcg_parse(*(A,_,[]),LO \ LO).
edcg_parse(*(A,X,[X|L]), LI \ LO) :- edcg_parse(A, LI \ L1), edcg_parse(*(A,X,L),L1 \ LO).
edcg_parse(*(A), LI \ LO) :- ((edcg_parse(A,LI \ L1), LI\=L1, edcg_parse(*A, L1 \ LO));LI=LO).
edcg_parse(+(A,X,[X|L]), LI \ LO) :- edcg_parse(A, LI \ L1), edcg_parse(*(A,X,L),L1 \ LO).
edcg_parse(+(A), LI \ LO) :- (edcg_parse(A,LI \ L1), LI\=L1, edcg_parse(*A,L1 \ LO)).
edcg_parse(?(A,_,E2,E2), LO \ LO).
edcg_parse(?(A,E1,_,E1), LI \ LO) :- edcg_parse(A, LI \ LO).
edcg_parse(?(A),LI \ LO) :- edcg_parse(A, LI \ LO);LI=LO.
edcg_parse((A;B), Tokens) :- edcg_parse(A, Tokens);edcg_parse(B, Tokens).
edcg_parse(#(A,N,X,L), LI \ LO) :- edcg_power(#(A,N,0,X,L),LI \ LO).

edcg_power(#(A,N,N,_,[]),LO \ LO).
edcg_power(#(A,N,M,X,[X|L]), LI \ LO) :- M1 is M+1, !,edcg_parse(A, LI \ L1), edcg_power(#(A,N,M1,X,L),L1 \ LO).
edcg_parse(^(A,N), LI \ LO) :- edcg_power(^(A,N,0),LI \ LO).
edcg_power(^(A,N,N),LO \ LO).
edcg_power(^(A,N,M), LI \ LO) :- M1 is M+1, !,edcg_parse(A, LI \ L1), edcg_power(^(A,N,M1),L1 \ LO).

edcg_nonterminal(X) :- list(X), !, fail.
edcg_nonterminal(_).

edcg_terminals(Xs) :- list(Xs).

edcg_phrase(Category, String, Left) :- edcg_parse(Category, String \ Left).
edcg_phrase(Category, [H | T]) :- edcg_parse(Category, [H | T] \ []).
edcg_phrase(Category,[]) :- edcg_parse(Category, [] \ []).
edcg_parse(A, Tokens) :- edcg_nonterminal(A), (A ==> B), edcg_parse(B, Tokens).
edcg_parse((A, B), Tokens \ Xs) :- edcg_parse(A, Tokens \ Tokens1), edcg_parse(B, Tokens1 \ Xs).
edcg_parse(A, Tokens) :- edcg_terminals(A), edcg_connect(A, Tokens).
edcg_parse({A}, Xs \ Xs) :- call(A).

edcg_connect([], Xs \ Xs).
edcg_connect([W | Ws], [W | Xs] \ Ys) :- edcg_connect(Ws, Xs \ Ys).
