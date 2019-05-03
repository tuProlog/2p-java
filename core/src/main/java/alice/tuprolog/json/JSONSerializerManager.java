package alice.tuprolog.json;

import alice.tuprolog.Term;
import alice.tuprolog.management.interfaces.JSONSerializerManagerMXBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

//Alberto
public class JSONSerializerManager implements JSONSerializerManagerMXBean {

    static Gson gson = new GsonBuilder() //Mandatory for serializing query obj!
                                         .registerTypeAdapter(Term.class, new JSONMarshaller())
                                         .create();
    /**
     * @author Alberto Sita
     */

    private static String currentAdapters = "alice.tuprolog.Term.class";

    public static String toJSON(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJSON(String jsonString, Class<T> klass) {
        return gson.fromJson(jsonString, klass);
    }

    ///Management

    @Override
    public String fetchCurrentAdapters() {
        return currentAdapters;
    }

    @Override
    public synchronized boolean addAdapter(String className) {
        try {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Term.class, new JSONMarshaller())
                    .registerTypeAdapter(Class.forName(className), new JSONMarshaller())
                    .create();
            currentAdapters = currentAdapters + " " + className + ".class";
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public synchronized void reset() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Term.class, new JSONMarshaller())
                .create();
        currentAdapters = "alice.tuprolog.Term.class";
    }

}
