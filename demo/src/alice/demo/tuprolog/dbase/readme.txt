run the demo with the tuProlog logic agents:

% without GUI
java alice.tuprolog.Agent alice/demo/tuprolog/dbase/MinPath.pl find_path('N1','N2').

% with swing GUI
java alice.tuprolog.Agent alice/demo/tuprolog/dbase/Main.pl


NOTE that the dbase 'distances.txt' must be seen as a user dbase:

on WINNT/WIN2000:
- open the control panel
- choose ODBC management
- choose user DSN
- add dbase 'distances' (selecting dir demo/tuprolog/dbase)
  provided with the example

change the dbase context with a simple text editor to test other cities and pathes.
start dbase cities are named N1,N2,...,N7
