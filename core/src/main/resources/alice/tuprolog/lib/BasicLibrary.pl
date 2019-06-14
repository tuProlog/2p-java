':-'(op( 1200, fx, ':-')). 
:- op(1200, xfx, ':-'). 
:- op(1200, fx, '?-'). 
:- op(1100, xfy, ';'). 
:- op(1050, xfy, '->'). 
:- op(1000, xfy, ','). 
:- op(900, fy, '\\+'). 
:- op(900, fy, 'not'). 
:- op(700, xfx, '='). 
:- op(700, xfx, '\\='). 
:- op(700, xfx, '=='). 
:- op(700, xfx, '\\=='). 
:- op(700, xfx, '@>'). 
:- op(700, xfx, '@<'). 
:- op(700, xfx, '@=<'). 
:- op(700, xfx, '@>='). 
:- op(700, xfx, '=:='). 
:- op(700, xfx, '=\\='). 
:- op(700, xfx, '>'). 
:- op(700, xfx, '<'). 
:- op(700, xfx, '=<'). 
:- op(700, xfx, '>='). 
:- op(700, xfx, 'is'). 
:- op(700, xfx, '=..'). 
:- op(500, yfx, '+'). 
:- op(500, yfx, '-'). 
:- op(500, yfx, '/\\'). 
:- op(500, yfx, '\\/'). 
:- op(400, yfx, '*'). 
:- op(400, yfx, '/'). 
:- op(400, yfx, '//'). 
:- op(400, yfx, '>>'). 
:- op(400, yfx, '<<'). 
:- op(400, yfx, 'rem'). 
:- op(400, yfx, 'mod'). 
:- op(200, xfx, '**'). 
:- op(200, xfy, '^'). 
:- op(200, fy, '\\').
:- op(200, fy, '-').
 
current_prolog_flag(Name, Value) :- 
    catch(get_prolog_flag(Name, Value), Error, false),
    !.
current_prolog_flag(Name, Value) :- 
    flag_list(L),
    member(flag(Name, Value), L).

'=:='(X, Y) :- expression_equality(X, Y).

'=\\='(X, Y) :- not expression_equality(X, Y).

'>'(X, Y) :- expression_greater_than(X, Y).

'<'(X, Y) :- expression_less_than(X, Y).

'>='(X, Y) :- expression_greater_or_equal_than(X, Y).

'=<'(X, Y) :- expression_less_or_equal_than(X, Y).

'=='(X, Y) :- term_equality(X, Y).

'\\=='(X, Y) :- not term_equality(X, Y).

'@>'(X, Y) :- term_greater_than(X, Y).

'@<'(X, Y) :- term_less_than(X, Y).

'@>='(X, Y) :- not term_less_than(X, Y).

'@=<'(X, Y) :- not term_greater_than(X, Y).

'=..'(T, [T]) :- atomic(T), !.

'=..'(T, L) :- compound(T),!, '$tolist'(T, L).

'=..'(T, L) :- nonvar(L), catch('$fromlist'(T, L), Error, false).

functor(Term, Functor, Arity) :-
    \+ var(Term), !,
    Term =.. [Functor | ArgList],
    length(ArgList, Arity).
functor(Term, Functor, Arity) :-
    var(Term),
    atomic(Functor),
    Arity == 0, !,
    Term = Functor.
functor(Term, Functor, Arity) :-
    var(Term),
    current_prolog_flag(max_arity, Max),
    Arity > Max, !,
    false.
functor(Term, Functor, Arity) :-
    var(Term),
    atom(Functor),
    number(Arity),
    Arity > 0, !,
    length(ArgList, Arity),
    Term =.. [Functor | ArgList].
functor(Term, Functor, Arity) :- false.

arg(N, C, T):- arg_guard(N, C, T), C =.. [_ | Args], element(N, Args, T).

clause(H, B) :- clause_guard(H, B), L = [], '$find'(H, L), copy_term(L, LC), member((':-'(H, B)), LC).

call(G) :- call_guard(G), '$call'(G).

'\\+'(P):- P,!, fail.

'\\+'(_).

C -> T ; B :- !,
    or((call(C), !, call(T)),
    '$call'(B)).
C -> T :-
    call(C), !,
    call(T).

or(A, B) :- '$call'(A).
or(A, B) :- '$call'(B).

A ; B :-
    A =.. ['->', C, T], !,
    ('$call'(C), !, '$call'(T) ; '$call'(B)).
A ; B :- '$call'(A).
A ; B :- '$call'(B).

unify_with_occurs_check(X, Y):- !, X = Y.

current_op(Pri, Type, Name):-get_operators_list(L), member(op(Pri, Type, Name), L).

once(X) :- myonce(X).

myonce(X) :- X, !.

repeat.

repeat :- repeat.

not(G) :- G, !, fail.
not(_). 

catch(Goal, Catcher, Handler) :- call(Goal).

findall(Template, Goal, Instances) :-
    all_solutions_predicates_guard(Template, Goal, Instances),
    L = [],
    '$findall0'(Template, Goal, L),
    Instances = L.

'$findall0'(Template, Goal, L) :-
    call(Goal),
    copy_term(Template, CL),
    '$append'(CL, L),
    fail.
'$findall0'(_, _, _).

variable_set(T, []) :- atomic(T), !. 
variable_set(T, [T]) :- var(T), !. 
variable_set([H | T], [SH | ST]) :- 
variable_set(H, SH), variable_set(T, ST). 
variable_set(T, S) :- 
    T =.. [_ | Args],
    variable_set(Args, L),
    flatten(L, FL),
    no_duplicates(FL, S),
    !.

flatten(L, FL) :- '$flatten0'(L, FL), !.

'$flatten0'(T, []) :- nonvar(T), T = [].
'$flatten0'(T, [T]) :- var(T). 
'$flatten0'([H | T], [H | FT]) :- not(islist(H)), !, '$flatten0'(T, FT).
'$flatten0'([H | T], FL) :-
    '$flatten0'(H, FH),
    '$flatten0'(T, FT),
    append(FH, FT, FL).

islist([]). 
islist([_ | L]) :- islist(L).

existential_variables_set(Term, Set) :-
    '$existential_variables_set0'(Term, Set), !.

'$existential_variables_set0'(Term, []) :- var(Term), !.
'$existential_variables_set0'(Term, []) :- atomic(Term), !. 
'$existential_variables_set0'(V ^ G, Set) :- 
    variable_set(V, VS),
    '$existential_variables_set0'(G, EVS),
    append(VS, EVS, Set).
'$existential_variables_set0'(Term, []) :- nonvar(Term), !. 

free_variables_set(Term, WithRespectTo, Set) :-
    variable_set(Term, VS),
    variable_set(WithRespectTo, VS1),
    existential_variables_set(Term, EVS1),
    append(VS1, EVS1, BV),
    list_difference(VS, BV, List), no_duplicates(List, Set), !.

list_difference(List, Subtrahend, Difference) :-
    '$ld'(List, Subtrahend, Difference).

'$ld'([], _, []). 
'$ld'([H | T], S, D) :- is_member(H, S), !, '$ld'(T, S, D).
'$ld'([H | T], S, [H | TD]) :- '$ld'(T, S, TD).

no_duplicates([], []). 
no_duplicates([H | T], L) :- is_member(H, T), !, no_duplicates(T, L). 
no_duplicates([H | T], [H | L]) :- no_duplicates(T, L).

is_member(E, [H | _]) :- E == H, !. 
is_member(E, [_ | T]) :- is_member(E, T).

'$wt_list'([], []). 
'$wt_list'([W + T | STail], [WW + T | WTTail]) :-
    '$wt_copyAndRetainFreeVar'(W, WW),
    '$wt_list'(STail, WTTail).

'$s_next'(Witness, WT_List, S_Next) :-
    copy_term(Witness, W2),
    '$s_next0'(W2, WT_List, S_Next), !.

bagof(Template, Goal, Instances) :- 
    all_solutions_predicates_guard(Template, Goal, Instances),
    free_variables_set(Goal, Template, Set),
    Witness =.. [witness | Set],
    iterated_goal_term(Goal, G),
    all_solutions_predicates_guard(Template, G, Instances),
    'splitAndSolve'(Witness, Instances, Template, G, Goal).

count([], 0). 
count([T1 | Ts], N) :- count(Ts, N1), N is (N1 + 1).

is_list(X) :- var(X), !, fail. 
is_list([]). 
is_list([_ | T]) :- is_list(T).

list_to_term([T1 | Ts], N) :-
    count(Ts, K),
    K == 0 ->
        N = T1, !
    ;
        list_to_term(Ts, N1),
        N = ';'(T1, N1), !.
list_to_term(Atom, Atom).

quantVar(X ^ Others, [X | ListOthers]) :- !,
    quantVar(Others, ListOthers).
quantVar(_Others, []). 

'splitAndSolve'(Witness, Instances, Template, G, Goal) :-
    splitSemicolon(G, L),
    variable_set(Template, TT),
    quantVar(Goal, Qvars),
    append(TT, Qvars, L1),
    'aggregateSubgoals'(L1, L, OutputList),
    member(E, OutputList),
    list_to_term(E, GoalE),
    findall(Witness + Template, GoalE, S),
    'bag0'(Witness, S, Instances).

splitSemicolon(';'(G1, Gs),[G1 | Ls]) :- !,
    splitSemicolon(Gs, Ls).
splitSemicolon(G1, [G1]).

aggregateSubgoals(Template, List, OutputList) :- 
    aggregateSubgoals(Template, List, [], [], OutputList).

aggregateSubgoals(Template, [H | T], CurrentAccumulator, Others, OutputList) :- 
    'occurs0'(Template, H) ->
        aggregateSubgoals(Template, T, [H | CurrentAccumulator], Others, OutputList)
    ;
        aggregateSubgoals(Template, T, CurrentAccumulator, [H | Others], OutputList).
aggregateSubgoals(_, [], CurrentAccumulator, NonAggregatedList, [Result1 | Result2]) :-
    reverse(CurrentAccumulator, Result1),
    reverse(NonAggregatedList, Result2).

occurs_member_list([], L) :- !, fail.
occurs_member_list([H | T], L) :-
    is_member(H, L) ->
        true, !
    ;
        occurs_member_list(T, L).
occurs_member_list_of_term(L, []):- !, fail.
occurs_member_list_of_term(Template, [H | T]) :-
    'occurs0'(Template, H) ->
        true, !
    ;
        occurs_member_list_of_term(Template, T).

'check_sub_goal'(Template, H, _Functor, Arguments) :-
    (
        (_Functor==';';_Functor==',') ->
            'occurs0'(Template, Arguments)
        ; (
            (_Functor=='.') ->
                occurs_member_list_of_term(Template, Arguments)
            ; occurs_member_list(Template, Arguments)
        )
    ).

'occurs0'(Template, H):-
    H =.. [_Functor | Arguments],
    'check_sub_goal'(Template, H, _Functor, Arguments).

'bag0'(_, [], _) :- !, fail.
'bag0'(Witness, S, Instances) :- 
    S == [] ->
        fail, !
    ;
        '$wt_list'(S, WT_List),
        '$wt_unify'(Witness, WT_List, Instances).

'bag0'(Witness, S, Instances) :-
    '$wt_list'(S, WT_List),
    '$s_next'(Witness, WT_List, S_Next),
    'bag0'(Witness, S_Next, Instances).

setof(Template, Goal, Instances) :- 
    all_solutions_predicates_guard(Template, Goal, Instances),
    bagof(Template, Goal, List),
    quicksort(List, '@<', OrderedList),
    no_duplicates(OrderedList, Instances).

forall(A, B) :- \+(call(A), \+ call(B)).

assert(C) :- assertz(C).

/*
retract(Rule) :-
    retract_guard(Rule),
    Rule = ':-'(Head, Body), !,
    clause(Head, Body),
    '$retract'(Rule).
retract(Fact) :-
    retract_guard(Fact),
    clause(Fact, true),
    '$retract'(Fact).
*/

retract(Clause) :- '$retract'(Clause).


retractall(Head) :-
    retract_guard(Head),
    findall(':-'(Head, Body), clause(Head, Body), L),
    '$retract_clause_list'(L), !.

'$retract_clause_list'([]). 
'$retract_clause_list'([E | T]) :- !,
    '$retract'(E),
    '$retract_clause_list'(T).

member(E, L) :-
    member_guard(E, L),
    member0(E, L).

member0(E, [E | _]).
member0(E, [_ | L]) :- member0(E, L).

length(L, S) :-
    number(S), !,
    lengthN(L, S), !.
length(L, S) :- var(S), lengthX(L, S).

lengthN([], 0). 
lengthN(_, N) :-
    N < 0, !,
    fail.
lengthN([_ | L], N) :-
    M is N - 1,
    lengthN(L, M).

lengthX([], 0).
lengthX([_ | L], N) :-
    lengthX(L, M),
    N is M + 1.

append([], L2, L2).
append([E | T1], L2, [E | T2]) :- append(T1, L2, T2).

reverse(L1, L2) :- reverse_guard(L1, L2), reverse0(L1, [], L2).

reverse0([], Acc, Acc).
reverse0([H | T], Acc, Y) :- reverse0(T, [H | Acc], Y).

delete(E, S, D) :-
    delete_guard(E, S, D),
    delete0(E, S, D).

delete0(E,[],[]).
delete0(E,[E | T], L) :- !, delete0(E, T, L).
delete0(E,[H | T],[H | L]) :- delete0(E, T, L).

element(P, L, E) :-
    element_guard(P, L, E),
    element0(P, L, E).

element0(1,[E | L], E):- !.
element0(N,[_ | L], E):-
    M is N - 1,
    element0(M, L, E).

quicksort([], Pred,[]).
quicksort([X | Tail], Pred, Sorted) :-
   split(X, Tail, Pred, Small, Big),                   
   quicksort(Small, Pred, SortedSmall),              
   quicksort(Big, Pred, SortedBig),                  
   append(SortedSmall, [X | SortedBig], Sorted).

split(_, [], _, [], []).
split(X, [Y | Tail], Pred, Small, [Y | Big]) :-
   Predicate =.. [Pred, X, Y],
   call(Predicate), !,
   split(X, Tail, Pred, Small, Big).                   
split(X,[Y | Tail], Pred,[Y | Small], Big):-             
   split(X, Tail, Pred, Small, Big).                   
