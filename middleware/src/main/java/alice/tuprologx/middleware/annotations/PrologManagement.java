package alice.tuprologx.middleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//Alberto
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrologManagement {
	
	/**
     * @author Alberto Sita
     * 
     */
	
	String host() default "localhost";
	int port() default 45000;
	boolean lazyBoot() default true; //ignored if @AsPrototype is used
//	String adaptor() default PrologMXBeanServer.HTTP_ADAPTOR;
	String credentialFile() default "";
	String SSLconfigFile() default "";
	
}
