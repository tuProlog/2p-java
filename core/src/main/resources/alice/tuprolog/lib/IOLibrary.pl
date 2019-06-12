consult(File) :-
    text_from_file(File,Text),
    add_theory(Text).

reconsult(File) :-
    text_from_file(File,Text),
    set_theory(Text).

solve_file(File,Goal) :-
    solve_file_goal_guard(File, Goal),
    text_from_file(File,Text),
    text_term(Text,Goal),
    call(Goal).

agent_file(X)  :-
    text_from_file(X,Y),agent(Y).