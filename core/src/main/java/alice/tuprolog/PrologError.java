package alice.tuprolog;

/**
 * @author Matteo Iuliani
 */
public class PrologError extends Throwable {

    private static final long serialVersionUID = 1L;

    // termine Prolog che rappresenta l'argomento di throw/1
    private Term error;
    private String descriptionError;

    public PrologError(Term error) {
        this.error = error;
    }

    /*Castagna 06/2011*/
	/*	
	sintassi da prevedere:
	TYPE	in	argument	ARGUMENT	of			GOAL				(instantiation, type, domain, existence, representation, evaluation)
	TYPE	in				GOAL										(permission, resource)
	TYPE	at clause#CLAUSE, line#LINE, position#POS: DESCRIPTION		(syntax)
	TYPE																(system)
	*/

    public PrologError(Term error, String descriptionError) {
        this.error = error;
        this.descriptionError = descriptionError;
    }

    public static PrologError type_error(EngineManager e, int argNo, String validType, Term culprit) {
        Term errorTerm = Struct.of("type_error", Struct.atom(validType), culprit);
        Term tuPrologTerm = Struct.of("type_error", e.getEnv().currentContext.currentGoal, Int.of(argNo), Struct.atom(validType), culprit);
        String descriptionError = "Type error" +
                                  " in argument " + argNo +
                                  " of " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError instantiation_error(EngineManager engineManager, int argNo) {
        Term errorTerm = Struct.atom("instantiation_error");
        Term tuPrologTerm = Struct.of("instantiation_error", engineManager.getEnv().currentContext.currentGoal, Int.of(argNo));
        String descriptionError = "Instantiation error" +
                " in argument " + argNo +
                " of " + engineManager.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError domain_error(EngineManager e, int argNo, String validDomain, Term culprit) {
        Term errorTerm = Struct.of("domain_error", Struct.atom(validDomain), culprit);
        Term tuPrologTerm = Struct.of("domain_error", e.getEnv().currentContext.currentGoal, Int.of(argNo), Struct.atom(validDomain), culprit);
        String descriptionError = "Domain error" +
                                  " in argument " + argNo +
                                  " of " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError existence_error(EngineManager e, int argNo, String objectType, Term culprit, Term message) {
        Term errorTerm = Struct.of("existence_error", Struct.atom(objectType), culprit);
        Term tuPrologTerm = Struct.of("existence_error", e.getEnv().currentContext.currentGoal, Int.of(argNo), Struct.atom(objectType), culprit, message);
        String descriptionError = "Existence error" +
                                  " in argument " + argNo +
                                  " of " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError permission_error(EngineManager e, String operation, String objectType, Term culprit, Term message) {
        Term errorTerm = Struct.of("permission_error", Struct.atom(operation), Struct.atom(objectType), culprit);
        Term tuPrologTerm = Struct.of("permission_error", e.getEnv().currentContext.currentGoal, Struct.atom(operation), Struct.atom(objectType), culprit, message);
        String descriptionError = "Permission error" +
                                  " in  " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError representation_error(EngineManager e, int argNo, String flag) {
        Term errorTerm = Struct.of("representation_error", Struct.atom(flag));
        Term tuPrologTerm = Struct.of("representation_error", e.getEnv().currentContext.currentGoal, Int.of(argNo), Struct.atom(flag));
        String descriptionError = "Representation error" +
                                  " in argument " + argNo +
                                  " of " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError evaluation_error(EngineManager e, int argNo, String error) {
        Term errorTerm = Struct.of("evaluation_error", Struct.atom(error));
        Term tuPrologTerm = Struct.of("evaluation_error", e.getEnv().currentContext.currentGoal, Int.of(argNo), Struct.atom(error));
        String descriptionError = "Evaluation error" +
                                  " in argument " + argNo +
                                  " of " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError resource_error(EngineManager e, Term resource) {
        Term errorTerm = Struct.of("resource_error", resource);
        Term tuPrologTerm = Struct.of("resource_error", e.getEnv().currentContext.currentGoal, resource);
        String descriptionError = "Resource error" +
                                  " in " + e.getEnv().currentContext.currentGoal.toString();
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError syntax_error(EngineManager e,
                                           int clause,
                                           int line, int position, Term message) {
        Term errorTerm = Struct.of("syntax_error", message);
        Term tuPrologTerm = Struct.of("syntax_error", e.getEnv().currentContext.currentGoal, Int.of(line), Int.of(position), message);

        int[] errorInformation = {clause, line, position};
        String[] nameInformation = {"clause", "line", "position"};
        String syntaxErrorDescription = message.getTerm().toString();

        {
            //Sostituzione degli eventuali caratteri di nuova linea con uno spazio
            syntaxErrorDescription = syntaxErrorDescription.replace("\n", " ");
            //Eliminazione apice di apertura e chiusura stringa
            syntaxErrorDescription = syntaxErrorDescription.substring(1, syntaxErrorDescription.length() - 1);
            String start = ("" + syntaxErrorDescription.charAt(0)).toLowerCase();
            //Resa minuscola l'iniziale
            syntaxErrorDescription = start + syntaxErrorDescription.substring(1);
        }

        String descriptionError = "Syntax error";

        boolean firstSignificativeInformation = true;
        for (int i = 0; i < errorInformation.length; i++) {
            if (errorInformation[i] != -1) {
                if (firstSignificativeInformation) {
                    descriptionError += " at " + nameInformation[i] + "#" + errorInformation[i];
                    firstSignificativeInformation = false;
                } else {
                    descriptionError += ", " + nameInformation[i] + "#" + errorInformation[i];
                }
            }
        }
        descriptionError += ": " + syntaxErrorDescription;

        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public static PrologError system_error(Term message) {
        Term errorTerm = Struct.atom("system_error");
        Term tuPrologTerm = Struct.of("system_error", message);
        String descriptionError = "System error";
        return new PrologError(Struct.of("error", errorTerm, tuPrologTerm), descriptionError);
    }

    public String toString() {
        return descriptionError != null ? descriptionError : error.toString();
    }

    public Term getError() {
        return error;
    }


}