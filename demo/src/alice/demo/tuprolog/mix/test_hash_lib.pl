test(0) :-
	!.
test(N) :-
	N1 is N - 1,
	test,
	test(N1).
test :-
	new_dict(D),
	dict_put(D,jinni,magic(1,2,3)),
	dict_get(D,jinni,Result),
	dict_put(D,mother_of(joe),'Mary'),
	dict_get(D,mother_of(joe),Mother),
	dict_remove(D,jinni),
	dict_remove(D,mother_of(joe)).
timed_test(N) :-
	class('java.lang.System') <- currentTimeMillis returns T0,
	test(N),
	class('java.lang.System') <- currentTimeMillis returns T1,
	DT is T1 - T0,
	write(DT),
	nl.

