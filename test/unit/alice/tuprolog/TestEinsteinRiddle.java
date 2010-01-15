/*
 * TestEinsteinRiddle.java
 *
 * Created on 4 maggio 2006, 16.12
 *
 */

package alice.tuprolog;

/**
 *
 * @author admin
 */
public class TestEinsteinRiddle {
    
    /** Creates a new instance of TestEinsteinRiddle */
    public TestEinsteinRiddle() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] str = new String[2];
        str[0] = "./alice/tuprolog/test/einsteinsRiddle.pl";
        str[1] = "einstein(_,X).";
        Agent.main(str);
    }
    
}
