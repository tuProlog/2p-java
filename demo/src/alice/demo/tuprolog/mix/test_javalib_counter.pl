%
% text mix for JavaLibrary
%
test_counter:-
    %
    % creating and using a counter
    %
    java_object('Counter',['ANiceCounter'],myCounter),
    myCounter <- setValue(5),
    myCounter <- inc,
    myCounter <- getValue returns Value,
    write(Value),nl,
    %
    % accessing static methods and static fields
    %
    class('Counter') <- getVersion returns Version,
    class('java.lang.System').out <- get(Out),
    Out <- println(Version),
    %
    % accessing public fields
    %
    myCounter.name <- get(Name),
    Out <- println(Name),
    myCounter.name <- set('NicerCounter'),
    %
    % creating Java arrays
    %
    java_object('Counter[]',[10],ArrayCounters),
    java_array_set(ArrayCounters, 3, myCounter),
    %
    java_array_length(ArrayCounters,Length),
    write_array(ArrayCounters,0,Length).


%
% writing the elements of a Java array
%
write_array(Array,Index,Length):-
    Index < Length,!,
    java_array_get(Array,Index,Element),
    write(element_value(Index,Element)),nl,
    NewIndex is Index + 1,
    write_array(Array,NewIndex,Length).

write_array(Array,Index,Length).
