package alice.tuprolog.management.interfaces;

//Alberto
public interface FlagManagerMXBean {

    /**
     * @author Alberto Sita
     */

    void reset();

    boolean occursCheckIsEnabled();

    boolean modifiable(String name);

    boolean configurePrologFlag(String name, String term);

    boolean validValue(String name, String term);

    String fetchAllPrologFlags();

    String fetchPrologFlag(String name);

}
