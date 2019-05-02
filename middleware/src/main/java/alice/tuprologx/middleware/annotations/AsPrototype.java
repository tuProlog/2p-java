package alice.tuprologx.middleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Alberto
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsPrototype {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	//marker

	//prevale su @AsSingleton
	
}
