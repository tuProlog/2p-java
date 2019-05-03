package alice.tuprolog.management.interfaces;

//Alberto
public interface JSONSerializerManagerMXBean {

    /**
     * @author Alberto Sita
     */

    void reset();

    String fetchCurrentAdapters();

    boolean addAdapter(String className);

}
