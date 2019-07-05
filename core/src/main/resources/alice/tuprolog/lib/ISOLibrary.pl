:- op(300, yfx,  'div').
:- op(400, yfx,  'mod').
:- op(400, yfx,  'rem').
:- op(200, fx,   'sin').
:- op(200, fx,   'cos').
:- op(200, fx,   'sqrt').
:- op(200, fx,   'atan').
:- op(200, fx,   'exp').
:- op(200, fx,   'log').

:- flag(bounded, [true, false], true, false).
:- flag(max_integer, [%%MAX_INT%%], %%MAX_INT%%, false).
:- flag(min_integer, [%%MIN_INT%%], %%MIN_INT%%, false).
:- flag(integer_rounding_function, [up, down], down, false).
:- flag(char_conversion, [on, off], off, false).
:- flag(debug, [on, off], off, false).
:- flag(max_arity, [%%MAX_ARITY%%], %%MAX_ARITY%%, false).
:- flag(undefined_predicate, [error, fail, warning], fail, false).
:- flag(double_quotes, [atom,chars,codes], atom, false).

bound(X) :- ground(X).

unbound(X) :- not(ground(X)).

atom_concat(F, S, R) :- var(R), !,
    atom_chars(F, FL),
    atom_chars(S, SL),
    append(FL, SL, RS),
    atom_chars(R, RS).

atom_concat(F, S, R) :-
    atom_chars(R, RS),
    append(FL, SL, RS),
    atom_chars(F, FL),
    atom_chars(S, SL).

chars_codes([], []).
chars_codes([X | L1], [Y | L2]) :-
    char_code(X, Y),
    chars_codes(L1, L2).

sub_atom(Atom, B, L, A, Sub) :-
    sub_atom_guard(Atom, B, L, A, Sub),
    sub_atom0(Atom, B, L, A, Sub).

sub_atom0(Atom, B, L, A, Sub) :-
    atom_chars(Atom, L1),
    sub_list(L2, L1, B),
    atom_chars(Sub, L2),
    length(L2, L),
    length(L1, Len),
    A is Len - (B + L).

sub_list([], _, 0).
sub_list([X | L1], [X | L2], 0) :-
    sub_list_seq(L1, L2).
sub_list(L1, [_ | L2], N) :-
    sub_list(L1, L2, M),
    N is M + 1.

sub_list_seq([], L).
sub_list_seq([X | L1], [X | L2]) :-
    sub_list_seq(L1, L2).

number_codes(Number, List) :-
    catch(number_codes0(Number, List), Error, false).

number_codes0(Number, List) :-
    nonvar(Number), !,
    num_atom(Number, Struct),
    atom_codes(Struct, List).
number_codes0(Number, List) :-
    atom_codes(Struct, List),
    num_atom(Number, Struct).
