spwaning heterogenous philosophers that interact through RMI middleware.
the prolog philosopher is spawned using Runner application and behaviour
specified in Philo.pl .
this demo illustrates how easy it is to exploit java objects and services 
(in this case RMI service to interact with Table object) from tuProlog.


a run example (being in boxmas directory)


spawning a table myTable with 5 forks:

	demo\tuprolog\philo\rmi\runTable  myTable 5


spawning a prolog philo named socrate, who uses forks labelled 0 and 1 at table myTable located at localhost:

	demo\demo\philo\rmi\runPrologPhilo socrate 0 1 myTable localHost
 

spawning a java philo named nietzsche, who uses forks labelled 1 and 2 at table home located at 192.0.0.3:

	demo\demo\philo\rmi\runJavaPhilo nietzsche 1 2 home 192.0.0.3






