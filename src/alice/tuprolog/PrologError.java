package alice.tuprolog;

/**
 * @author Matteo Iuliani
 */
public class PrologError extends Throwable {

	// termine Prolog che rappresenta l'argomento di throw/1
	private Term error;

	public PrologError(Term error) {
		this.error = error;
	}

	public Term getError() {
		return error;
	}

	public static PrologError instantiation_error(EngineManager e, int argNo) {
		Term errorTerm = new Struct("instantiation_error");
		Term tuPrologTerm = new Struct("instantiation_error",
				e.env.currentContext.currentGoal, new Int(argNo));
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError type_error(EngineManager e, int argNo,
			String validType, Term culprit) {
		Term errorTerm = new Struct("type_error", new Struct(validType),
				culprit);
		Term tuPrologTerm = new Struct("type_error",
				e.env.currentContext.currentGoal, new Int(argNo), new Struct(
						validType), culprit);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError domain_error(EngineManager e, int argNo,
			String validDomain, Term culprit) {
		Term errorTerm = new Struct("domain_error", new Struct(validDomain),
				culprit);
		Term tuPrologTerm = new Struct("domain_error",
				e.env.currentContext.currentGoal, new Int(argNo), new Struct(
						validDomain), culprit);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError existence_error(EngineManager e, int argNo,
			String objectType, Term culprit, Term message) {
		Term errorTerm = new Struct("existence_error", new Struct(objectType),
				culprit);
		Term tuPrologTerm = new Struct("existence_error",
				e.env.currentContext.currentGoal, new Int(argNo), new Struct(
						objectType), culprit, message);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError permission_error(EngineManager e,
			String operation, String objectType, Term culprit, Term message) {
		Term errorTerm = new Struct("permission_error", new Struct(operation),
				new Struct(objectType), culprit);
		Term tuPrologTerm = new Struct("permission_error",
				e.env.currentContext.currentGoal, new Struct(operation),
				new Struct(objectType), culprit, message);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError representation_error(EngineManager e, int argNo,
			String flag) {
		Term errorTerm = new Struct("representation_error", new Struct(flag));
		Term tuPrologTerm = new Struct("representation_error",
				e.env.currentContext.currentGoal, new Int(argNo), new Struct(
						flag));
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError evaluation_error(EngineManager e, int argNo,
			String error) {
		Term errorTerm = new Struct("evaluation_error", new Struct(error));
		Term tuPrologTerm = new Struct("evaluation_error",
				e.env.currentContext.currentGoal, new Int(argNo), new Struct(
						error));
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError resource_error(EngineManager e, Term resource) {
		Term errorTerm = new Struct("resource_error", resource);
		Term tuPrologTerm = new Struct("resource_error",
				e.env.currentContext.currentGoal, resource);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError syntax_error(EngineManager e, int line,
			int position, Term message) {
		Term errorTerm = new Struct("syntax_error", message);
		Term tuPrologTerm = new Struct("syntax_error",
				e.env.currentContext.currentGoal, new Int(line), new Int(
						position), message);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

	public static PrologError system_error(Term message) {
		Term errorTerm = new Struct("system_error");
		Term tuPrologTerm = new Struct("system_error", message);
		return new PrologError(new Struct("error", errorTerm, tuPrologTerm));
	}

}
