package alice.tuprologx.middleware.LPaaS;

import java.lang.reflect.Field;
import java.util.ArrayList;

import alice.util.Tools;
import com.google.gson.Gson;

import alice.tuprolog.Prolog;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.exceptions.InvalidTheoryException;
import alice.tuprolog.exceptions.NoMoreSolutionException;
import alice.tuprologx.middleware.LPaaS.annotations.GoalsToMatch;
import alice.tuprologx.middleware.LPaaS.interfaces.LPaaS_ClientInterface;
import alice.tuprologx.middleware.LPaaS.interfaces.LPaaS_ConfiguratorInterface;
import alice.tuprologx.middleware.annotations.PostConstructCall;
import alice.tuprologx.middleware.annotations.PostMigrationSetup;


//Alberto
public abstract class LPaaSComponent implements LPaaS_ClientInterface, LPaaS_ConfiguratorInterface {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	private Field engine = null;
	private ArrayList<Term> goalsToMatch = null;
	private String[] goals = null;
	
	@PostConstructCall
	@PostMigrationSetup
	public final void componentSetup(){
		GoalsToMatch toMatch = null;
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field field : fields){
			if(field.getType().equals(Prolog.class)){
				field.setAccessible(true);
				engine = field;
				if(field.isAnnotationPresent(GoalsToMatch.class)){
					toMatch = field.getAnnotation(GoalsToMatch.class);
					break;
				}
			}
		}
		goals = toMatch.toMatch();
		goalsToMatch =  new ArrayList<Term>();
		for(String goal : goals){
			Term term = Term.createTerm(goal, getProlog().getOperatorManager());
			goalsToMatch.add(term);
		}
			
	} 
	
	public synchronized final Prolog getProlog(){
		try {
			return (Prolog) engine.get(this);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public synchronized final String solve(String toSolve){
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				return getProlog().solve(term).toJSON();
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String solveN(String toSolve, int num){
		ArrayList<String> result = new ArrayList<String>();
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				result.add(getProlog().solve(term).toJSON());
				for(int i=1; i<num; i++){
					try{
						result.add(getProlog().solveNext().toJSON());
					} catch (Exception ex){
						break;
					}
				}
				return new Gson().toJson(result);
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String solveN(String toSolve, int num, long time){
		ArrayList<String> result = new ArrayList<String>();
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				result.add(getProlog().solve(term, time).toJSON());
				for(int i=1; i<num; i++){
					try{
						result.add(getProlog().solveNext(time).toJSON());
					} catch (Exception ex){
						break;
					}
				}
				return new Gson().toJson(result);
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String solveAll(String toSolve){
		ArrayList<String> result = new ArrayList<String>();
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				result.add(getProlog().solve(term).toJSON());
				for(;;){
					try{
						result.add(getProlog().solveNext().toJSON());
					} catch (Exception ex){
						break;
					}
				}
				return new Gson().toJson(result);
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String getGoalList(){
		ArrayList<String> result = new ArrayList<String>();
		for(String goal : goals)
			result.add(goal);
		return new Gson().toJson(result);
	}
	
	@Override
	public synchronized final String solveAll(String toSolve, long time){
		ArrayList<String> result = new ArrayList<String>();
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				result.add(getProlog().solve(term, time).toJSON());
				for(;;){
					try{
						result.add(getProlog().solveNext(time).toJSON());
					} catch (Exception ex){
						break;
					}
				}
				return new Gson().toJson(result);
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String solve(String toSolve, long time){
		try{
			Term term = Term.createTerm(Tools.removeApices(toSolve), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed){
				return getProlog().solve(term, time).toJSON();
			} else {
				return "Goal: "+toSolve+" NOT allowed!";
			}
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String addGoal(String goal){
		try{
			Term term = Term.createTerm(Tools.removeApices(goal), getProlog().getOperatorManager());
			for(Term t : goalsToMatch){
				if(t.match(term))
					return "Goal already authorized!";
			}
			goalsToMatch.add(term);
			return "yes.";
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String removeGoal(String goal){
		String res = "Goal not present!";
		try{
			Term term = Term.createTerm(Tools.removeApices(goal), getProlog().getOperatorManager());
			Term toRemove = null;
			for(Term t : goalsToMatch){
				if (t.match(term)){
					toRemove = t;
					res = "yes.";
					break;
				}
			}
			if(toRemove != null)
				goalsToMatch.remove(toRemove);
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
		return res;
	}
	
	@Override
	public synchronized final String isGoal(String goal){
		try{
			Term term = Term.createTerm(Tools.removeApices(goal), getProlog().getOperatorManager());
			boolean allowed = false;
			for(Term toMatch : goalsToMatch){
				if(toMatch.match(term)){
					allowed = true;
					break;
				}
			}
			if(allowed)
				return "yes.";
			else
				return "no.";
		} catch (Exception e) {
			return "An exception has occured. Please check your request and retry!";
		}
	}
	
	@Override
	public synchronized final String addTheory(String theory){
		try {
			getProlog().addTheory(Theory.parseLazilyWithStandardOperators(alice.util.Tools.removeApices(theory)));
		} catch (InvalidTheoryException e) {
			e.printStackTrace();
			return "no.";
		}
		return "yes.";
	}

	@Override
	public synchronized final String solveNext(){
		try {
			return getProlog().solveNext().toJSON();
		} catch (NoMoreSolutionException e) {
			e.printStackTrace();
		}
		return "No more solution available.";
	}
	
	@Override
	public synchronized final String solveNext(long time){
		try {
			return getProlog().solveNext(time).toJSON();
		} catch (NoMoreSolutionException e) {
			e.printStackTrace();
		}
		return "No more solution available.";
	}
	
}
