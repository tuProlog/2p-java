package alice.tuprolog.factories;

import alice.tuprolog.lib.BasicLibrary;
import alice.tuprolog.lib.IOLibrary;
import alice.tuprolog.lib.ISOLibrary;
import alice.tuprolog.lib.OOLibrary;

//Alberto
public final class DefaultLibrariesSet {

    /**
     * @author Alberto Sita
     */

    public static String[] getDefaultLibrariesSetForCurrentPlatform() {
        if (System.getProperty("java.vm.name").equals("IKVM.NET")) {
            return new String[]{
                    BasicLibrary.class.getName(), ISOLibrary.class.getName(),
                    IOLibrary.class.getName(), "OOLibrary.OOLibrary, OOLibrary"};
        } else {
            return new String[]{BasicLibrary.class.getName(), ISOLibrary.class.getName(),
                    IOLibrary.class.getName(), OOLibrary.class.getName()};
        }
    }
}
