Let's sketch a typical demo session:

first spawn a table agent;  in this case we spawn an agent 
loading the theory contained in the file demo/tuprolog/philo/tucson2/tucson_table.pl, 
demonstrating the goal  table(lab10,localhost,4). the agent provides philosopher resources 
(in this case we want 5 chops: 0..4)  using  a tuple centre called lab10 located 
locally.   

Note that the tuple centre is built (if no already present) at first access.

    java tuprolog.Agent 
                         demo/philo/tucson2/tucson_table.pl 
                         table(lab10,localhost,4).

then spawn the philosophers – every philosopher must activated specifyng its name 
and what forks it uses:  

    java tuprolog.Agent 
                          demo/philo/tucson2/tucson_philo.pl
                          philo(lab10,localhost,cledes,0,1).
 
In this case we spawn a philosopher named cledes that use forks labelled 0 and 1 
at the tuple centre called lab10 at localhost.
For 5 forks typically you spawn 5 philosophers:  here we spawn two other 
philosophers in order to have the exploiting of 0,1,2 forks:

    java tuprolog.Agent 
                          demo/philo/tucson2/tucson_philo.pl
                          philo(lab10,localhost,mario,1,2).

    java tuprolog.Agent 
                          demo/philo/tucson2/tucson_philo.pl
                          philo(lab10,localhost,giulia,2,0).

Note that without programming properly the coordination medium the agents would 
deadlock,  because of they compete  for common resources
(cledes: chops 0,1 – mario: chops 1,2 – giulia: chops 2,0).
