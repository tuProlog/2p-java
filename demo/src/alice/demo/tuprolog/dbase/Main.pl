%
% demo about accessing a jdbc cities dbase 
% valueting min path among cities specified
% from a swing GUI 
%
% the entry point is main predicate
%
% NOTE ABOUT the GUI:
% the GUI component class Form has been defined outside
% in order to exploit visual builder facilities
% here we create and deploy an instance of that component
%

:-solve(main).

main:-
    init_gui(Form),
    init_dbase('jdbc:odbc:distances','','',Connection),
    loop(Form,Connection).

loop(Form,Connection):-
     Form <- getInput returns InputForm,
     InputForm.from <- get(FromCity),
     InputForm.to <- get(ToCity),
     string_atom(FromCity, From), 
     string_atom(ToCity, To),
     find_path(Connection, From, To, MinList, MinLength),
     Form <- reset,
     outputResult(Form, From, To, MinList, MinLength),
     abolish(distance(_,_,_)),!,
     loop(Form, Connection).

loop(_,_):-
    stdout <- println('there are problems in accessing the data base.').

% intelligence 

find_path(Connection,From, To, MinList, MinLength):-
    exec_query( Connection, 'SELECT city_from, city_to, distance FROM distances.txt', ResultSet),
    assert_result(ResultSet),
    findall( pa(Length,L) , pathes(From,To,L,Length), NewList),
    current_prolog_flag(max_integer,Max),
    min_path(NewList, pa(Max,_), pa(MinLength, MinList)).

pathes(A,B,List,Length):-    
    path(A,B,List,Length,[]). 

path(A,A,[],0,_).
path(A,B,[X|LN],Length,Visited):-    
    distance(A,X,Length1),
    not(member(X,Visited)),
    path(X,B,LN,Length2,[X|Visited]),
    Length is Length1 + Length2.

min_path([],X,X):-!.
min_path([ pa(Length, List) | L ], pa(MinLength, MinList), Result):-
    Length < MinLength, !, min_path(L, pa(Length,List), Result).
min_path([_|L],X, Result):- min_path(L, X, Result).



% dbase interface

init_dbase( DBase, Username, Password, Connection):-    
    class('java.lang.Class') <- forName('sun.jdbc.odbc.JdbcOdbcDriver'),
    class('java.sql.DriverManager') <- getConnection(DBase,Username,Password) returns Connection.

exec_query( Connection, Query, ResultSet ):-
    Connection <- createStatement returns Statement,
    Statement <- executeQuery( Query ) returns ResultSet.


% storing retrieved info as facts

assert_result( ResultSet ) :-
    ResultSet <- next returns true, !,
    ResultSet <- getString('city_from') returns From,
    ResultSet <- getString('city_to') returns To,
    ResultSet <- getInt('distance') returns Dist,
    assert(distance(From, To, Dist)),
    assert_result(ResultSet).
assert_result(_).



% gui interface

init_gui(Form):-
    java_object('alice.demo.tuprolog.dbase.Form',[],Form),
    Form <- setSize(320,180),
    Form <- show.

writeList(_,[]):-!.
writeList(Form, [X|L]):-
    Form <- write(', '), 
    Form <- write(X),!,
    writeList(Form, L).

outputResult(Form,From,To,[],_):-!,
    Form <- write('no path found from '), 
    Form <- write(From), 
    Form <- write(' to '),
    Form <- write(To), 
    Form <- writeln.

outputResult(Form,From,To,MinList,MinLength):-
    Form <- writeln, 
    Form <- write('min path from '),
    Form <- write(From),
    Form <- write(' to '), 
    Form <- write(To), 
    Form <- write(': '),
    Form <- writeln,
    Form <- write(From), 
    writeList(Form, MinList),
    Form <- writeln,
    Form <- writeln, Form <- write('Length: '), 
    java_object('java.lang.Integer',[MinLength],Len),
    Len <- toString returns SLen, 
    Form <-write(SLen).




    

   

    

    
