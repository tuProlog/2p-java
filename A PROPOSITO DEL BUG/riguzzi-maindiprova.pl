
main:-
	see("testodiprova.txt"),
	write('file found'),
	read(A),
	write(A),nl,
	(read(B)->
		write(B),nl
	;
		write(fail),nl
	),
	seen.