:-load_library('tuprolog.lib.TucsonLibrary').

table(TCName, TCAddress, N):-
        TCName @ TCAddress ? set_spec(
                                [ reaction(in(chops(C1,C2)), (pre, out_r(required(C1,C2)))), 
                                  reaction(in(chops(C1,C2)), (post,in_r(required(C1,C2)))),
                                  reaction(out_r(required(C1,C2)),(in_r(chop(C1)),in_r(chop(C2)),out_r(chops(C1,C2)))),
                                  reaction(out(chops(C1,C2)), ( in_r(chops(C1,C2)),out_r(chop(C1)),out_r(chop(C2)))),
                                  reaction(out_r(chop(C1)), (rd_r(required(C1,C)), in_r(chop(C1)),in_r(chop(C)),out_r(chops(C1,C)))),
                                  reaction(out_r(chop(C2)), (rd_r(required(C,C2)), in_r(chop(C)),in_r(chop(C2)),out_r(chops(C,C2)))) ]),
        write('reactions provided'),nl,
        out_chop(TCName,TCAddress,N),
        'java.lang.Thread' <- currentThread returns T, T <- sleep((2000 as long)).

out_chop(TCName, TCAddress, 0):- !,
        TCName @ TCAddress ? out(chop(0)),
        write('chop 0 provided'),nl.

out_chop(TCName, TCAddress, N):-
        TCName @ TCAddress ? out(chop(N)),
        write('chop '),write(N),write(' provided'), nl,
        M is N - 1, out_chop(TCName, TCAddress, M).

