%
% demo about using JDBC from tu-prolog
% 
% computing the min path among two specified cities
% fetching data from a dbase
% (the distances dbase consulted is 'distances')
%

% the entry point is find_path

find_path(From, To):-
    init_dbase('jdbc:odbc:distances', Connection,'',''),
    exec_query( Connection, 'SELECT city_from, city_to, distance FROM distances.txt', ResultSet),
    assert_result(ResultSet),
    findall( pa(Length,L) , pathes(From,To,L,Length), NewList),
    current_prolog_flag(max_integer,Max),
    min_path(NewList, pa(Max,_), pa(MinLength, MinList)),
    outputResult(From,To,MinList,MinLength).

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

writeList([]):-!.
writeList([X|L]):-write(', '),write(X),!,writeList(L).

outputResult(From,To,[],_):-!,
    write('no path found from '),write(From),write(' to '),write(To),nl.

outputResult(From,To,MinList,MinLength):-
    write('min path from '),write(From),write(' to '),write(To),
    write(': '),write(From),writeList(MinList),write('  - length: '), write(MinLength).

% dbase interface

init_dbase( DBase, Username, Password, Connection):-    
    class('java.lang.Class') <- forName('sun.jdbc.odbc.JdbcOdbcDriver'),
    class('java.sql.DriverManager') <- getConnection(DBase,Username,Password) returns Connection,
    write('[ dbase '),write(DBase),write(' connected ]'),nl.

exec_query( Connection, Query, ResultSet ):-
    Connection <- createStatement returns Statement,
    Statement <- executeQuery( Query ) returns ResultSet,
    write('[ query '),write(Query),write(' executed ]'),nl.

assert_result( ResultSet ) :-
    ResultSet <- next returns true,!,
    ResultSet <- getString('city_from') returns From,
    ResultSet <- getString('city_to') returns To,
    ResultSet <- getInt('distance') returns Dist,
    assert(distance(From, To, Dist)),
    assert_result(ResultSet).

assert_result(_).

init_gui:-
    java_object('demo.tuprolog.dbase.Form', [], Form),
        Form <- setSize(380,280),
        Form <- show.

    

   

    

    
