spwaning heterogenous philosophers that interact through CORBA middleware.
the prolog philosopher is spawned using Runner application and behaviour
specified in Philo.pl .
this demo illustrates how easy it is to exploit java objects and services 
(in this case CORBA service to interact with Table object) from tuProlog.


a run example (being in boxmas directory)

first spawn the ORB daemon:

        start tnameserv

then spaw a table myTable with 5 forks:

	demo\tuprolog\philo\corba\runTable  myTable 5

then spawn happy heterogenous philosophers:

spawning a prolog philo named kant, who uses forks labelled 0 and 1 at table myTable located at localhost:

	demo\tuprolog\philo\corba\runPrologPhilo kant 0 1 myTable localHost
 

spawning a java philo named nietzsche, who uses forks labelled 1 and 2 at table home located at 192.0.0.3:

	demo\tuprolog\philo\corba\runJavaPhilo nietzsche 1 2 home 192.0.0.3






