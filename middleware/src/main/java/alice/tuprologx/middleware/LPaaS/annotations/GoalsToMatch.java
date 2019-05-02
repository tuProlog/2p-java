package alice.tuprologx.middleware.LPaaS.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Alberto
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GoalsToMatch {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	String[] toMatch();

}
