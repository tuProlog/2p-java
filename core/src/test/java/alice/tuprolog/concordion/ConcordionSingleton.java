package alice.tuprolog.concordion;

import alice.tuprolog.*;
import alice.tuprolog.exceptions.InvalidLibraryException;
import alice.tuprolog.exceptions.PrologException;
import alice.tuprolog.interfaces.event.ExceptionListener;

import java.util.ArrayList;
import java.util.List;

public class ConcordionSingleton {

    private static ConcordionSingleton singleton;
    private String exceptionFounded = "";
    private boolean exFounded = false; // variable used to found an exception
    private ExceptionListener ex = e -> {
        exFounded = true;
        exceptionFounded = e.getMsg();
    };

    private ConcordionSingleton() {
    }

    private static void loadAllLibraries(Prolog engine, String[] libraryNames) {
        try {
            if (libraryNames.length > 0) {
                for (String libraryName : libraryNames) {
                    engine.loadLibrary(libraryName);
                }
            }
        } catch (InvalidLibraryException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConcordionSingleton getInstance() {
        if (singleton == null) {
            singleton = new ConcordionSingleton();
        }

        return singleton;
    }

    /* If there isn't a theory, insert null in the tag <td> */
    public boolean success(String goal, String theory, String... libraryNames) throws Exception {

        Prolog engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        SolveInfo info = engine.solve(goal);
        return info.isSuccess();
    }

    /* Return true if there is an exception */
    public boolean successWithException(String goal, String theory, String... libraryNames) throws PrologException {

        Prolog engine = null;
        @SuppressWarnings("unused")
        SolveInfo info = null;
        exFounded = false;
        engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        engine.addExceptionListener(ex);
        info = engine.solve(goal);
        //System.out.println(engine.getErrors());
        return exFounded;

    }

    /* Return type of error */
    public String successWithExceptionAndText(String goal, String theory, String... libraryNames) throws PrologException {

        Prolog engine = null;
        @SuppressWarnings("unused")
        SolveInfo info = null;
        exFounded = false;
        engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        engine.addExceptionListener(ex);
        info = engine.solve(goal);
        if (exFounded) {
            return exceptionFounded;
        } else {
            return "No Errors!";
        }

    }

    /* Return the first result (With or Without replace) */
    public String successAndResult(String goal, String theory, String variable, String... libraryNames) throws Exception {

        return successAndResultVerifyReplace(goal, theory, variable, true, libraryNames);

    }

    public String successAndResultWithoutReplace(String goal, String theory, String variable, String... libraryNames) throws Exception {

        return successAndResultVerifyReplace(goal, theory, variable, false, libraryNames);

    }

    /* Return the first result of goal (With or Without replace), with limit */
    public boolean successAndResultsWithLimit(String goal, String theory,
                                              String variable, String solution, int maxSolutions, String... libraryNames) throws Exception {

        return successAndResultsWithLimitVerifyReplace(goal, theory, variable,
                                                       solution, true, maxSolutions, libraryNames);

    }

    public boolean successAndResultsWithLimitWithoutReplace(String goal,
                                                            String theory, String variable, String solution,
                                                            int maxSolutions, String... libraryNames) throws Exception {

        return successAndResultsWithLimitVerifyReplace(goal, theory, variable,
                                                       solution, false, maxSolutions, libraryNames);

    }

    /* Check the result in the list(infinite) */
    private boolean successAndResultsWithLimitVerifyReplace(String goal,
                                                            String theory, String variable, String solution, boolean replace,
                                                            int maxSolutions, String... libraryNames) throws Exception {

        Prolog engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        SolveInfo info = null;
        List<String> results = new ArrayList<String>();


        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        info = engine.solve(goal);
        while (info.isSuccess() && maxSolutions != 0) {

            for (Var var : info.getBindingVars()) {

                if ((var.toString()).startsWith(variable)) {

                    variable = (replace ? replaceForVariable(
                            var.toString(), ' ') : var.toString());

                    Term t = info.getVarValue(variable);
                    results.add(replace ? replaceUnderscore(t.toString()) : t.toString());

                }

            }

            if (replace) {
                variable = replaceForVariable(variable, '_');
            }
            Term t = info.getVarValue(variable);
            results.add(replace ? replaceUnderscore(t.toString()) : t.toString());

            if (engine.hasOpenAlternatives()) {
                info = engine.solveNext();
            } else {
                break;
            }
            maxSolutions--;
        }

        //System.out.println(results.toString());
        return results.contains(solution);

    }

    private boolean successAndResultsVerifyReplace(String goal, String theory,
                                                   String variable, String solution, boolean replace, String... libraryNames) throws Exception {

        Prolog engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        SolveInfo info = null;
        List<String> results = new ArrayList<String>();

        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        info = engine.solve(goal);
        while (info.isSuccess()) {

            for (Var var : info.getBindingVars()) {
                if ((var.toString()).startsWith(variable)) {

                    variable = replaceForVariable(var.toString(), ' ');
                    Term t = info.getVarValue(variable);
                    results.add(replace ? replaceUnderscore(t.toString()) : t.toString());
                }

            }
            variable = replaceForVariable(variable, '_');
            Term t = info.getVarValue(variable);
            results.add(replace ? replaceUnderscore(t.toString()) : t.toString());

            if (engine.hasOpenAlternatives()) {
                info = engine.solveNext();
            } else {
                break;
            }
        }
        System.out.println(results.toString());
        return results.contains(solution);

    }

    /* Check the result in the list(not infinite) */
    public boolean successAndResults(String goal, String theory,
                                     String variable, String solution, String... libraryNames) throws Exception {

        return successAndResultsVerifyReplace(goal, theory, variable, solution,true, libraryNames);

    }

    public boolean successAndResultsWithoutReplace(String goal, String theory,
                                                   String variable, String solution, String... libraryNames) throws Exception {

        return successAndResultsVerifyReplace(goal, theory, variable, solution,false, libraryNames);

    }

    /* Return the first result of goal */
    private String successAndResultVerifyReplace(String goal, String theory,
                                                 String variable, boolean replace, String... libraryNames) throws Exception {

        Prolog engine = new Prolog();
        loadAllLibraries(engine, libraryNames);
        if (!theory.equalsIgnoreCase("null")) {
            engine.setTheory(Theory.parseLazilyWithStandardOperators(theory));
        }
        SolveInfo info = engine.solve(goal);
        for (Var var : info.getBindingVars()) {
            if ((var.toString()).startsWith(variable)) {

                variable = replaceForVariable(var.toString(), ' ');
                Term t = info.getVarValue(variable);
                System.out.println(t.toString());
                return (replace ? replaceUnderscore(t.toString()) : t.toString());

            }

        }
        variable = replaceForVariable(variable, '_');
        Term t = info.getVarValue(variable);
        return (replace ? replaceUnderscore(t.toString()) : t.toString());
    }

    private String replaceForVariable(String query, char car) {

        String result = "";
        for (int i = 0; i < query.length(); i++) {

            if (query.charAt(i) == car) {
                return result;
            }
            result += (query.charAt(i) + "");

        }

        return result;

    }

    private String replaceUnderscore(String query) {

        String result = "";
        boolean trovato = false;
        for (int i = 0; i < query.length(); i++) {

            if (query.charAt(i) == ',' || query.charAt(i) == ')'
                || query.charAt(i) == ']') {
                trovato = false;
            }
            if (!trovato) {
                result += (query.charAt(i) + "");
            }
            if (query.charAt(i) == '_') {
                trovato = true;
            }

        }

        return result;
    }


    public Term value(String evaluable) throws Exception {
        Prolog engine = new Prolog();
        SolveInfo result = engine.solve("X is " + evaluable);
        return result.getVarValue("X");
    }


}
