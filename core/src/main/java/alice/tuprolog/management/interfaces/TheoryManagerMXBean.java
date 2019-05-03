package alice.tuprolog.management.interfaces;

import alice.tuprolog.exceptions.InvalidTheoryException;

//Alberto
public interface TheoryManagerMXBean {

    /**
     * @author Alberto Sita
     */

    String fetchMostRecentConsultedTheory();

    String fetchKnowledgeBase(boolean all);

    void clearKnowledgeBase();

    void consultTheory(String theory, boolean dynamicTheory, String libName) throws InvalidTheoryException;

    void assertA(String clause, boolean dyn, String libName, boolean backtrackable);

    void assertZ(String clause, boolean dyn, String libName, boolean backtrackable);

    void retract(String clause);

    void removeLibraryTheory(String libName);

}
