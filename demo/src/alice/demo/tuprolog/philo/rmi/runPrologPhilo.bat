rem %1 philo name
rem %2 left fork
rem %3 right fork
rem %4 table name
rem %5 table address
start java -Djava.security.policy=policy.all tuprolog.Agent demo/tuprolog/philo/rmi/Philo.pl go(%1,'rmi://%5/%4',%2,%3).



