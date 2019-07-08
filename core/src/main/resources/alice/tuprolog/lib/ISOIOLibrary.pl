'$stream'(S) :-
    '$input_streams'(SS),
    member(S, SS).
'$stream'(S) :-
    '$output_streams'(SS),
    member(S, SS).

/*
stream_property(S,P) :- find_property(L,P), member(S,L).
*/
stream_property(S, P) :-
    '$stream'(S),
    '$stream_properties'(S, PS),
    member(P, PS).