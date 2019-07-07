:- op(800, xfx, '<-').
:- op(850, xfx, 'returns').
:- op(200, xfx, 'as').
:- op(600, xfx, '.'). 

new_object_bt(ClassName, Args, Id) :- 
    new_object(ClassName, Args, Id).
new_object_bt(ClassName, Args, Id) :-
    destroy_object(Id).

Obj <- What :-
    java_call(Obj, What, Res),
    Res \== false.
Obj <- What returns Res :-
    java_call(Obj, What, Res).

array_set(Array, Index, Object) :-
    class('java.lang.reflect.Array') <- set(Array as 'java.lang.Object', Index, Object as 'java.lang.Object'), !.
/*
array_set(Array, Index, Object) :-
    java_catch(
        (class('java.lang.reflect.Array') <- set(Array as 'java.lang.Object', Index, Object as 'java.lang.Object'), !),
        [
            (E, java_array_set_primitive(Array, Index, Object))
            %(E, fail)
        ],
        true
    ).
*/
array_set(Array, Index, Object) :-
    java_array_set_primitive(Array, Index, Object).

array_get(Array, Index, Object) :-
    class('java.lang.reflect.Array') <- get(Array as 'java.lang.Object', Index) returns Object, !.
/*
array_get(Array, Index, Object) :-
    java_catch(
        (class('java.lang.reflect.Array') <- get(Array as 'java.lang.Object', Index) returns Object, !),
        [
            (E, java_array_get_primitive(Array, Index, Object))
            %(E, fail)
        ],
        true
    ).
*/
array_get(Array, Index, Object) :-
    java_array_get_primitive(Array, Index, Object).


array_length(Array, Length) :-
    class('java.lang.reflect.Array') <- getLength(Array as 'java.lang.Object') returns Length.

java_object_bt(ClassName, Args, Id) :-
    java_object(ClassName, Args, Id).
java_object_bt(ClassName, Args, Id) :-
    destroy_object(Id).

java_array_set(Array, Index, Object) :-
    class('java.lang.reflect.Array') <- set(Array as 'java.lang.Object', Index, Object as 'java.lang.Object'), !.
/*
java_array_set(Array, Index, Object) :-
    java_catch(
        (class('java.lang.reflect.Array') <- set(Array as 'java.lang.Object', Index, Object as 'java.lang.Object'), !),
        [
            (E, java_array_set_primitive(Array, Index, Object))
            %(E, fail)
        ],
        true
    ).
*/
java_array_set(Array, Index, Object) :-
    java_array_set_primitive(Array, Index, Object).

java_array_get(Array, Index, Object) :-
    class('java.lang.reflect.Array') <- get(Array as 'java.lang.Object', Index) returns Object, !.
/*
java_array_get(Array, Index, Object) :-
    java_catch(
        (class('java.lang.reflect.Array') <- get(Array as 'java.lang.Object', Index) returns Object, !),
        [
            (E, java_array_get_primitive(Array, Index, Object))
            %(E, fail)
        ],
        true
    ).
*/
java_array_get(Array, Index, Object) :-
    java_array_get_primitive(Array, Index, Object).

java_array_length(Array, Length) :-
    class('java.lang.reflect.Array') <- getLength(Array as 'java.lang.Object') returns Length.

java_object_string(Object, String) :-
    Object <- toString returns String.

java_catch(JavaGoal, List, Finally) :-
    call(JavaGoal),
    call(Finally).