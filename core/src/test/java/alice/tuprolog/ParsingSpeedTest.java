package alice.tuprolog;

import alice.tuprolog.exceptions.InvalidTermException;

public class ParsingSpeedTest {

    public static void main(String[] args) throws InvalidTermException {
        int repetitions = 1000;
        long start = System.currentTimeMillis();
        OperatorManager om = OperatorManager.standardOperators();
        for (int i = 0; i < repetitions; i++) {
            Term.createTerm("A ; B :- A =.. ['->', C, T], !, (C, !, T ; B)", om);
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Time parsing " + repetitions + " terms: " + time + " milliseconds.");
    }

}
