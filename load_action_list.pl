load_actions(Filename,Actions):-write('0'),nl,
			see(Filename),
write('1'),nl,
            read_all_actions(Stream,All_actions),nl,
			write('2'),nl,
            seen,
			write('3'),nl,
			identify_actions(All_actions,ActionNames),
			write('4'),nl,
			identify_originators_att(All_actions,ActionNames,Actions),
			write('5'),nl,
			write_actions(Actions).

identify_actions(Actions,AN):-
	setof(A,(O,Att)^member(activity(A,O,Att),Actions),AN).
	
identify_originators_att(_Actions,[],[]).

identify_originators_att(Actions,[H|T],[activity(H,LO,LA)|T1]):-
	setof(O,Att^member(activity(H,O,Att),Actions),LO),
	setof(LAtt,O^member(activity(H,O,LAtt),Actions),LLAV),
	append_all(LLAV,[],LAVD),
	%remove_duplicates(LAVD,LAV),
	list_to_set(LAVD,LAV),
	identify_att_val(LAV,LA),	
	identify_originators_att(Actions,T,T1).


append_all([],L,L).

append_all([H|T],LIn,LOut):-
	append(LIn,H,L1),
	append_all(T,L1,LOut).

identify_att_val([],[]):-!.

identify_att_val(LAV,LA):-
	setof(AN,(Val,A)^(member(A,LAV),A=..[AN|Val]),LAN),
	identify_val(LAN,LAV,LA).
	
identify_val([],_LAV,[]).

identify_val([H|T],LAV,[A|T1]):-
	Att=..[H,Val],
	setof(Val,member(Att,LAV),LV),
	A=..[H,LV],
	identify_val(T,LAV,T1).

	

read_all_actions(Stream,Actions):-
            (read(X) -> 
              read_model(Stream,Model_actions),             
              read_all_actions(Stream,Other_actions),
              append(Model_actions,Other_actions,Actions)
          ;
              Actions=[]

            ).

read_model(Stream,Actions):-
             read(X),
             check(Stream,X,Actions).

check(_,end(model(_)),[]):-
             !.

check(Stream,hap(performed(activity(X,Y,Z)), _, _),[activity(X,Y,Z)|T]):-
             !,
             read_model(Stream,T).

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% MODIFICHE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

check(Stream,hap(V, _, _),[activity(W,Y,Z)|T]):-
			!,
			V=..[Event,activity(X,Y,Z)],
			%term_to_atom(Event,E1),
			%atom_codes(Event,E1),
			%term_to_atom(X,X1),
			%atom_codes(X,X1),
			concat_atom([Event,X],'_',W),
			read_model(Stream,T).


/*
check(Stream,hap(V, _, _),[activity(W,Y,Z)|T]):-
			!,
			V=..[Event,activity(X,Y,Z)],
			W=..[Event,X],
			read_model(Stream,T).         
*/

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% MODIFICHE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%           
        
check(Stream,_,X):-
            read_model(Stream,X).

write_actions(Actions):-
            tell('actionslist.kb'),
            write_line(Stream,Actions),
            told.

write_line(_Stream,[]):-
            !.

write_line(Stream,[H|T]):-
            write(H),
            print("."),
		    nl,
            write_line(Stream,T).
						
			
list_to_set([],[]).

list_to_set([H|T],T1):-
	member(H,T),!,
	list_to_set(T,T1).

list_to_set([H|T],[H|T1]):-
	list_to_set(T,T1).