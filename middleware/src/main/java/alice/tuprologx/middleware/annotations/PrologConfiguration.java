package alice.tuprologx.middleware.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

//Alberto
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologConfiguration {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	String prologTheory() default "";
	String[] directives() default "";
	String[] fromFiles() default "";
	
}
