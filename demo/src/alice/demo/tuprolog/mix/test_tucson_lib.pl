
:- load_library('alice.tuprologx.lib.TucsonLibrary').

test(TC, N):-
    produce(TC, N),
    consume(TC).


produce(TC,N):-
    N > 0,
    TC ? out(pippo(N)),
    %
    write('out tuple '),write(pippo(N)),nl,
    %
    N1 is N - 1,
    produce(TC,N1).

produce(_,0).


consume(TC):-
    TC ? inp(pippo(X)),!,
    %
    write('in tuple '),write(pippo(X)),nl,
    %
    consume(TC).

consume(TC).

    
    

    