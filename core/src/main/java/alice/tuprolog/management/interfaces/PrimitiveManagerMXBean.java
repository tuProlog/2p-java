package alice.tuprolog.management.interfaces;

//Alberto
public interface PrimitiveManagerMXBean {

    /**
     * @author Alberto Sita
     */

    String fetchDirectiveInfo(String directive);

    String fetchFunctorInfo(String functor);

    String fetchPredicateInfo(String predicate);

}
